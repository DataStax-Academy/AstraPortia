package com.datastax.astraportia.dao;

import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * The class is a client to interact with Stargate Document API.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Component
public class StargateClient {

    /** Logger for the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StargateClient.class);
   
    /** Header for authToken. */
    public static final String HEADER_CASSANDRA = "X-Cassandra-Token";
    
    /** Value in second. */
    public static final int TTL_SECONDS = 5 * 60;
    
    @Value("${stargate.url}")
    private String url;
    
    @Value("${stargate.username}")
    private String username;
    
    @Value("${stargate.password}")
    private String password;
    
    @Value("${stargate.namespace}")
    private String namespace;
    
    /** Handle the authToken as an internal argument.*/
    private String authToken             = null;
    private int    authTokenTTLSeconds   = TTL_SECONDS;
    private long   authTokenCreationDate = 0;
    
    /** Core Java Http Client. **/
    private HttpClient httpClient;
    
    /** Jackson Mapper. */
    private ObjectMapper objectMapper;
    
    /** Default Constructor. */
    public StargateClient() {
        initJacksonObjectMapper();
        initHttpClient();
    }
    
    /**
     * Constructor full arguments.
     * We could have use a Builder quickly with this 6 args.
     */
    public StargateClient(String url, String username, String password, 
                          String namespace, int authTokenTTL) {
        this();
        this.url = url;
        this.username = username;
        this.password = password;
        this.namespace = namespace;
        this.authTokenTTLSeconds = authTokenTTL;
    }
    
    /** Generate an Authentication Token for the queries. */
    public String getAuthToken() {
        if ((System.currentTimeMillis() - authTokenCreationDate) > (authTokenTTLSeconds * 1000)) {
            LOGGER.info("Renewing AuthToken...");
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(uriAuthenticate())
                        .timeout(Duration.ofMinutes(1))
                        .header("Content-Type", "application/json")
                        .POST(BodyPublishers.ofString(bodyAuthenticate())).build();
                HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
                if (HttpStatus.OK.value() == response.statusCode()) {
                   authToken = (String) objectMapper.readValue(response.body(), Map.class).get("authToken");
                   authTokenCreationDate = System.currentTimeMillis();
                   LOGGER.info("[OK] - Success, token will live for {} second(s)", authTokenTTLSeconds);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("An error occured", e);
            }
        }
        return authToken;
    }
    
    /**
     * Wrap 2 resources to create document (PUT and POST). If an id is provided we
     * use the PUT, otherwise POST
     * 
     * @param <D>
     *      serializable object to convert as JSON  
     * @param authToken
     *      authentication token required in headers
     * @param doc
     *      document to be serialized (using Jackson)
     * @param docId
     *      optional unique identifier for the document
     * @param collectionName
     *      collection name
     * @return
     *      return document Id (the one provided eventually)
     */
    public <D extends Serializable> String create(StargateDocument<D> doc) {
        try {
            // Creating Req
            Builder reqBuilder = HttpRequest.newBuilder()
                    .uri(uriCreateNewDocument(doc))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .header(HEADER_CASSANDRA, getAuthToken());

            // PUT to create a new enforcing Id,POST to create a new with no id 
            if (doc.getDocumentId().isEmpty()) {
                reqBuilder.POST(BodyPublishers.ofString(objectMapper.writeValueAsString(doc.getData())));
            } else {
                reqBuilder.PUT(BodyPublishers.ofString(objectMapper.writeValueAsString(doc.getData())));
            }
            
            // Call
            HttpResponse<String> response = httpClient.send(reqBuilder.build(), BodyHandlers.ofString());
            if (null !=response && (
                    response.statusCode() == HttpStatus.CREATED.value() || 
                    response.statusCode() == HttpStatus.OK.value())) {
                return (String) objectMapper.readValue(response.body(), Map.class).get("documentId");
            } else {
                throw new IllegalArgumentException(response.body());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
    }
   
    public <B extends StargateDocument<?>> Optional<B> findById(String collectioName, String docId, Class<B> clazz) {
        try {
            // Call
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(uriFindDocumentById(docId, collectioName))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .header(HEADER_CASSANDRA, getAuthToken())
                    .GET().build();
            HttpResponse<String> response = httpClient.send(req, BodyHandlers.ofString());
            if (null !=response && response.statusCode() == HttpStatus.OK.value()) {
                return Optional.of(objectMapper.readValue(response.body(), clazz));
            } else if (response.statusCode() == HttpStatus.NOT_FOUND.value()) {
                throw new IllegalArgumentException("Invalid id, the document has not been found: " 
                        + response.body());
            } else {
                throw new IllegalArgumentException(response.body());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
    }
    
    /**
     * When executing a search a data object is returned containing a set of ID and only the
     * values expected.
     *
     * @param authToken
     * @param collectionName
     * @param propertyName
     * @param propertyValue
     * @return
     */
    public Set<String> findAllIds(String collectionName, String key) {
        return findIds(uriFindAllIds(collectionName, key));
    }
    
    public Set<String> findIdsByPropertyValue(String collectionName, String propertyName, String propertyValue) {
        return findIds(uriFindByPropertyValue(collectionName, propertyName, propertyValue));
    }
    
    @SuppressWarnings("unchecked")
    private Set<String> findIds(URI uri) {
        Objects.requireNonNull(uri);
        try {
            HttpResponse<String> response = httpClient.send(HttpRequest.newBuilder()
                    .uri(uri).timeout(Duration.ofMinutes(1))
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .header(HEADER_CASSANDRA, getAuthToken())
                    .GET().build(), BodyHandlers.ofString());
            // Call
            if (null !=response && response.statusCode() == HttpStatus.OK.value()) {
                Map<String, Object> o = (Map<String, Object>) objectMapper.readValue(response.body(), Map.class).get("data");
                return o.keySet();
            } else {
                throw new IllegalArgumentException(response.body());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
    }
    
    /**
     * Build URL to GET all ids with 'exists' OP.
     */
    private URI uriFindAllIds(String colName, String key) {
        Objects.requireNonNull(colName);
        Objects.requireNonNull(key);
        return UriComponentsBuilder.fromUriString(urlStargateEndpoint() 
                + "/v2/namespaces/" + namespace + "/collections/" + colName)
                .queryParam("where", "{\"" + key + "\": {\"$exists\": true}}")
                .build().encode().toUri();
    }
    
    private URI uriFindByPropertyValue(String colName, String propName, String propValue) {
        // Building search Query
        return UriComponentsBuilder.fromUriString(urlStargateEndpoint() 
                + "/v2/namespaces/" + namespace + "/collections/" + colName)
                .queryParam("where", "{\"" + propName + "\": {\"$eq\": \"" + propValue + "\"}}")
                .build().encode().toUri();
    }
    
    private String urlStargateEndpoint() {
        return url;
    }
    
    private URI uriAuthenticate() {
        return URI.create(urlStargateEndpoint() + "/v1/auth/");
    }
    
    private String bodyAuthenticate() {
        return "{ "
                + "\"username\": \""+ username +"\", "
                + "\"password\": \""+ password + "\"}";
    }
    
    private URI uriCreateNewDocument(StargateDocument<?> sDoc) {
        String url = urlStargateEndpoint() 
                + "/v2/namespaces/" + namespace
                + "/collections/" + sDoc.getCollectionName() + "/";
        if (sDoc.getDocumentId().isPresent()) {
            url = url + sDoc.getDocumentId().get();
        }
        return URI.create(url);
    }
    
    private URI uriFindDocumentById(String docId, String collectionName) {
        return URI.create(urlStargateEndpoint() 
                + "/v2/namespaces/" + namespace
                + "/collections/" + collectionName + "/" + docId);
    }
    
    private void initJacksonObjectMapper() {
        // Setup Jackson
           objectMapper = new ObjectMapper();
           objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
           objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
           objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
           objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
           objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
           objectMapper.setDateFormat(new SimpleDateFormat("dd/MM/yyyy"));
           objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector());
           LOGGER.info("Jackson serializer has been initialized");
    } 
       
    private void initHttpClient() {
        httpClient = HttpClient.newBuilder()
                   .version(Version.HTTP_2)
                   .followRedirects(Redirect.NORMAL)
                   .build();
        LOGGER.info("Http Client has been initialized");
    }
    
    /**
     * Getter accessor for attribute 'objectMapper'.
     *
     * @return
     *       current value of 'objectMapper'
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
    
    /**
     * Getter accessor for attribute 'keyspace'.
     *
     * @return
     *       current value of 'keyspace'
     */
    public String getNameSpace() {
        return namespace;
    }
}

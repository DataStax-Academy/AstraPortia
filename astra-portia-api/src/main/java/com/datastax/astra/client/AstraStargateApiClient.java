package com.datastax.astra.client;

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
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

@Component
public class AstraStargateApiClient {

    /** Logger for the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AstraStargateApiClient.class);
   
    /** Header for authToken. */
    public static final String HEADER_CASSANDRA = "X-Cassandra-Token";
    
    @Value("${astra.stargate.db-id}")
    private String dbId;
    
    @Value("${astra.stargate.region-id}")
    private String regionId;
    
    @Value("${astra.stargate.username}")
    private String username;
    
    @Value("${astra.stargate.password}")
    private String password;
    
    @Value("${astra.stargate.keyspace}")
    private String keyspace;
    
    private HttpClient httpClient;
    
    private ObjectMapper objectMapper;
    
    public AstraStargateApiClient() {
        // Setup HTTP client
        httpClient = HttpClient.newBuilder()
                .version(Version.HTTP_2)
                .followRedirects(Redirect.NORMAL)
                .build();
        LOGGER.info("Http Client has been initialized");
        
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
    
    public AstraStargateApiClient(String dbId, String regionId, 
            String username, String password, String keyspace) {
        this();
        this.dbId = dbId;
        this.regionId = regionId;
        this.username = username;
        this.password = password;
        this.keyspace = keyspace;
    }
    
    @SuppressWarnings("unchecked")
    public Optional<String> authentiticate() {
        try {
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getAstratUrlAuth()))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString("{ "
                            + "\"username\": \""+ username +"\", "
                            + "\"password\": \""+ password + "\"}")).build();
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            if (null !=response && response.statusCode() == HttpStatus.OK.value()) {
                Map<String, String> repo = objectMapper.readValue(response.body(), Map.class);
                return Optional.ofNullable(repo.get("authToken"));
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
    }
    
    public <D extends Serializable> String createDocument(D doc, 
            Optional<String> docId, 
            String authToken, String collectionName) {
        try {
            Builder reqBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(getAstraUrlCreateNewDocument(docId, collectionName)))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .header(HEADER_CASSANDRA, authToken);

            // PUT to create a new enforcing Id,POST to create a new with no id 
            if (docId.isEmpty()) {
                reqBuilder.POST(BodyPublishers.ofString(objectMapper.writeValueAsString(doc)));
            } else {
                reqBuilder.PUT(BodyPublishers.ofString(objectMapper.writeValueAsString(doc)));
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

    public <D extends Serializable>  ArrayList getAllDocument(
            String authToken,
            String collectionName) {

        return new ArrayList();
    }
   
    public Set<String> searchDocumentIds(String authToken, String collectionName, String propertyName, String propertyValue) {
        try {
            // Call
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(getAstraUrlSearchDocuments(collectionName, propertyName, propertyValue)))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .header(HEADER_CASSANDRA, authToken)
                    .GET().build();
            
            HttpResponse<String> response = httpClient.send(req, BodyHandlers.ofString());
            // Call
            if (null !=response && response.statusCode() == HttpStatus.OK.value()) {
                Map<String, Object> o = (Map<String, Object>) objectMapper.readValue(response.body(), Map.class).get("data");
                System.out.println(o);
                return o.keySet();
            } else {
                throw new IllegalArgumentException(response.body());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
    }
    
    public Optional<String> readDocument(String authToken, String collectioName, String docId) {
        try {
            // Call
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(getAstraUrlFindDocumentById(docId, collectioName)))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .header(HEADER_CASSANDRA, authToken)
                    .GET().build();
            HttpResponse<String> response = httpClient.send(req, BodyHandlers.ofString());
            if (null !=response && response.statusCode() == HttpStatus.OK.value()) {
                return Optional.of(response.body());
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
    
    private String getAstraApiUrlCore() {
        StringBuilder sb = new StringBuilder();
        sb.append("https://").append(dbId).append("-").append(regionId);
        sb.append(".apps.astra.datastax.com/api/rest");
        return sb.toString();
    }
    
    private String getAstratUrlAuth() {
        return getAstraApiUrlCore() + "/v1/auth/";
    }
    
    private String getAstraUrlCreateNewDocument(Optional<String> docId, String collectionName) {
        String url = getAstraApiUrlCore() + "/v2/namespaces/" + keyspace + "/collections/" + collectionName + "/";
        if (docId.isPresent()) {
            url = url + docId.get();
        }
        return url;
    }
    
    private String getAstraUrlFindDocumentById(String docId, String collectionName) {
        return getAstraApiUrlCore() 
                + "/v2/namespaces/" + keyspace
                + "/collections/" + collectionName + "/" + docId;
    }
    
    private String getAstraUrlSearchDocuments(String collectionName, String proname, String proValue) {
        return getAstraApiUrlCore() 
                + "/v2/namespaces/" + keyspace
                + "/collections/" + collectionName + "?where={\"" + 
                proname + "\": {\"$eq\": \"" + proValue + "\"}}";
        
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
     * Getter accessor for attribute 'dbId'.
     *
     * @return
     *       current value of 'dbId'
     */
    public String getDbId() {
        return dbId;
    }

    /**
     * Getter accessor for attribute 'regionId'.
     *
     * @return
     *       current value of 'regionId'
     */
    public String getRegionId() {
        return regionId;
    }

    /**
     * Getter accessor for attribute 'keyspace'.
     *
     * @return
     *       current value of 'keyspace'
     */
    public String getKeyspace() {
        return keyspace;
    }
}

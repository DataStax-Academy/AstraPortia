package com.datastax.astraportia;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.datastax.astraportia.dao.AstraPortiaServices;
import com.datastax.astraportia.dao.StargateClient;
import com.datastax.astraportia.neo.Neo;
import com.datastax.astraportia.neo.NeoDoc;

/**
 * Validation of {@link StargateClient} with {@link Neo} and a Json Dataset.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@RunWith(JUnitPlatform.class)
@SpringJUnitConfig
@ContextConfiguration(classes = {StargateClient.class, AstraPortiaServices.class})
@TestPropertySource(locations = "/application-test.properties")
public class StargateClientTest {
    
    /** Logger for the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StargateClientTest.class);
   
    @Autowired
    private StargateClient client;
    
    @BeforeClass
    public static void logStart() {
        LOGGER.info("=========================================");
        LOGGER.info("Start Tests on the stargate client");
        LOGGER.info("Please update src/test/resources/application-test.properties");
        LOGGER.info("=========================================");
    }
    
    @Test
    @DisplayName("When looking for token, one is generated")
    public void should_generate_AuthToken() {
        Assertions.assertNotNull(client.getAuthToken());
    }
    
    @Test
    @DisplayName("When creating doc, providing id, same id is returned by API.")
    public void should_write_doc_using_provided_id() {
        // Given
        UUID myDocId = UUID.randomUUID();
        // When
        NeoDoc doc = new NeoDoc(new Neo(), myDocId.toString());
        // Then if returned is the expected one
        Assertions.assertEquals(client.create(doc), myDocId.toString());
    }
    
    @Test
    @DisplayName("When creating a doc, reading the doc get back initial object")
    public void should_write_doc_and_generate_id() {
        // Given
        Neo neo = new Neo();
        neo.setDesignation("toto");
        neo.setDiscoveryDate("2020-01-01");
        neo.setH_mag("2.0");
        // When
        Assertions.assertNotNull(client.create(new NeoDoc(neo)));
    }   
    
    @Test
    @DisplayName("When creating a doc, reading the doc get back initial object")
    public void should_read_samedata_as_written() {
        // Given
        Neo neo = new Neo();
        neo.setDesignation("sample Object");
        // When
        NeoDoc doc = new NeoDoc(neo);
        String docId = client.create(doc);
        // When
        Optional<NeoDoc> doc2 = client.findById(doc.getCollectionName(), docId, NeoDoc.class);
        // Then, we expect to have the same
        Assertions.assertTrue(doc2.isPresent());
        Assertions.assertEquals(doc2.get().getDocumentId().get(), docId);
        Assertions.assertEquals(neo.getDesignation(), doc2.get().getData().getDesignation());
    }
    
    @Test
    @DisplayName("Search all docs_Id with flag is 1")
    public void should_get_ids_from_propertyValue() {
        // When
        Set<String> ids = client.findIdsByPropertyValue(NeoDoc.NEO_DOC, "flag", "1");
        // Then
        Assertions.assertFalse(ids.isEmpty());
    }
    
    @Test
    @DisplayName("Search all docs with property designation (PK) = findAll")
    public void should_get_ids_from_propertyExists() {
        // When
        Set<String> ids = client.findAllIds(NeoDoc.NEO_DOC, "flag");
        // Then
        Assertions.assertFalse(ids.isEmpty());
    }

}

package com.datastax.astraportia.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.datastax.astraportia.neo.Neo;
import com.datastax.astraportia.neo.NeoDoc;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Implementation of service for the User Interface.
 *
 * @author DataStax Clavis Team
 */
@Component
public class AstraPortiaServices {
    
    /** Logger for the class. */
    private static final Logger logger = LoggerFactory.getLogger(AstraPortiaServices.class);
    
    /** Collection Name chosen for the Near_EARTH_OBJECT. */
    public static final String NEAR_EARTH_OBJECT_COLLECTION = "neo_doc";
   
    /** Where the Magic Happen. */
    private StargateClient stargateClient;
    
    /** Injection client for service. */
    public AstraPortiaServices(StargateClient client) {
        this.stargateClient = client;
    }
    
    /**
     * Import Data from File.
     */
    public void importNeoJsonDataSet(String jsonFilename) {
        try {
           importNeoJsonDataSet(new FileInputStream(new File(jsonFilename)));
        } catch (Exception e) {
            throw new IllegalArgumentException("DataSet", e);
        }
    }
    
    /**
     * Import Data from Stream (thinking about uploading a doc form UI)
     */
    public void importNeoJsonDataSet(InputStream in) {
        try {
            TypeReference<List<Neo>> tr = new TypeReference<List<Neo>>() {};
            List<Neo> dataset = stargateClient.getObjectMapper().readValue(in, tr);
            logger.info("Dataset loaded with {} items", dataset.size());
            
            for (Neo neo : dataset) {
                String doc = stargateClient.create(new NeoDoc(neo));
                logger.info("Document created with id {}" , doc);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("DataSet", e);
        }
    }
    
    /**
     * Create a Doc for {@link Neo} enforcing identifier (search)
     */
    public String createNeo(Neo doc, String docId) {
        return stargateClient.create(new NeoDoc(doc, docId));
    }
    
    public List<NeoDoc> findAllNeos() {
        return stargateClient
              .findAllIds(NeoDoc.NEO_DOC, "designation") // all objects have property 'designation'
              .parallelStream()
              .map(id -> stargateClient.findById(NeoDoc.NEO_DOC, id, NeoDoc.class)) // load full doc
              .filter(Optional::isPresent)
              .map(Optional::get)
              .collect(Collectors.toList()); 
    }
    
}

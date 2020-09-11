package com.datastax.astra.client;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datastax.astra.nearearthobject.NearEarthObject;
import com.datastax.astra.nearearthobject.NearEarthObjectStargateWrapper;
import com.fasterxml.jackson.core.type.TypeReference;

@Component
public class AstraPortiaServices {
    
    /** Logger for the class. */
    private static final Logger logger = LoggerFactory.getLogger(AstraPortiaServices.class);
    
    public static final String NEAR_EARTH_OBJECT_COLLECTION = "neo_doc";
   
    @Autowired
    private AstraStargateApiClient stargateClient;
    
    public  List<NearEarthObject> parseNearEarthObjectsDataSet(String jsonFilename) {
        try {
            List<NearEarthObject> dataset = stargateClient.getObjectMapper()
                    .readValue(new FileInputStream(new File(jsonFilename)), new TypeReference<List<NearEarthObject>>() {});
            logger.info("Dataset loaded with {} items", dataset.size());
            return dataset;
        } catch (Exception e) {
            throw new IllegalArgumentException("DataSet", e);
        }
    }
    
    public String createNearEarthObject(String docId, String authToken, NearEarthObject doc) {
        return stargateClient.createDocument(doc, Optional.ofNullable(docId), authToken, NEAR_EARTH_OBJECT_COLLECTION);
    }
    
    public void importNearEarthObjectsDataSet(String authToken, List<NearEarthObject> data) {
        for (NearEarthObject neo : data) {
            String doc = stargateClient.createDocument(neo, Optional.empty(), authToken, NEAR_EARTH_OBJECT_COLLECTION);
            logger.info("Document created with id {}" , doc);
        }
    }
    
    public Optional<NearEarthObjectStargateWrapper> findNearEarthObjectById(String authToken, String docid) {
        Optional<String> doc = stargateClient.readDocument(authToken, NEAR_EARTH_OBJECT_COLLECTION, docid);
        if (doc.isPresent()) {
            try {
                return Optional.ofNullable(
                        stargateClient.getObjectMapper().readValue(doc.get(), NearEarthObjectStargateWrapper.class));
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot parse result ", e);
            } 
        }
        return Optional.empty();
        
    }
    
    
    
    
}

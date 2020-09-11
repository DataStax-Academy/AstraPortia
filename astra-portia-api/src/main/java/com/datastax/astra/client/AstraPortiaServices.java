package com.datastax.astra.client;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datastax.astra.nearearthobject.NearEarthObject;
import com.fasterxml.jackson.core.type.TypeReference;

@Component
public class AstraPortiaServices {
    
    /** Logger for the class. */
    private static final Logger logger = LoggerFactory.getLogger(AstraPortiaServices.class);
   
    @Autowired
    private AstraStargateApiClient stargateClient;
    
    public void importNearEarthObjects(String jsonFilename) {
        try {
            List<NearEarthObject> dataset = stargateClient.getObjectMapper()
                    .readValue(new FileInputStream(new File(jsonFilename)), new TypeReference<List<NearEarthObject>>() {});
            logger.info("Dataset loaded with {} items", dataset.size());
            
            
        } catch (Exception e) {
            throw new IllegalArgumentException("DataSet", e);
        }
    }
}

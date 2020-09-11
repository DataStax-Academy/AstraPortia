package com.datastax.astra;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.datastax.astra.client.AstraPortiaServices;
import com.datastax.astra.client.AstraStargateApiClient;
import com.datastax.astra.nearearthobject.NearEarthObject;

@RunWith(JUnitPlatform.class)
@SpringJUnitConfig
@ContextConfiguration(classes = {AstraStargateApiClient.class, AstraPortiaServices.class})
@TestPropertySource(locations = "/application-test.properties")
public class StargateTest {
    
    public static final String dataset = "src/main/resources/2020_09_10_near_earth_asteroids_and_comets.json";
  
    @Autowired
    private AstraStargateApiClient stargateClient;
    
    @Autowired
    private AstraPortiaServices services;
    
    @Test
    public void shoud_return_an_authToken_() {
        Optional<String> authToken = stargateClient.authentiticate();
        Assertions.assertTrue(authToken.isPresent());
    }
    
    @Test
    public void should_dataset_exist() {
        List<NearEarthObject> data = services.parseNearEarthObjectsDataSet(dataset);
        Assertions.assertNotNull(data);
        Assertions.assertFalse(data.isEmpty());
        Assertions.assertEquals(202, data.size());
    }
    
    @Test
    public void importNearEarthObjectDataset() {
        services.importNearEarthObjectsDataSet(
                stargateClient.authentiticate().get(), 
                services.parseNearEarthObjectsDataSet(dataset));
    }

}

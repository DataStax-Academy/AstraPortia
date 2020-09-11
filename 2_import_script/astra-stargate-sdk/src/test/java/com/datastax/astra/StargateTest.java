package com.datastax.astra;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.datastax.astra.client.AstraStargateApiClient;

@RunWith(JUnitPlatform.class)
@SpringJUnitConfig
@ContextConfiguration(classes = AstraStargateApiClient.class)
@TestPropertySource(locations = "/application-test.properties")
public class StargateTest {
  
    @Autowired
    private AstraStargateApiClient stargateClient;
    
    @Test
    public void testAuthenticate() {
        Optional<String> authToken = stargateClient.authentiticate();
        authToken.ifPresent(System.out::println);
    }

}

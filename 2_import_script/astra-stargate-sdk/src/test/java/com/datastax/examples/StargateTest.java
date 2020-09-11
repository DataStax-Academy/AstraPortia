package com.datastax.examples;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.datastax.astra.client.AstraStargateApiClient;

public class StargateTest {
    
    String dbId         = "eabba33b-dc22-4055-bced-e84e87cfa92c";
    String regionId     = "europe-west1";
    String username     = "gateuser";
    String password     = "gatepassword";
    String keyspace     = "stargate";
    
    @Test
    public void testAuthenticate() {
        AstraStargateApiClient stargateClient = new AstraStargateApiClient(
                dbId, regionId, username, password, keyspace);
        Optional<String> authToken = stargateClient.authentiticate();
        authToken.ifPresent(System.out::println);        
        
    }

}

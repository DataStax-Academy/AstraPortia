package com.datastax.astra.nearearthobject;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datastax.astra.client.AstraStargateApiClient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(
  methods = {PUT, POST, GET, OPTIONS, DELETE, PATCH},
  maxAge = 3600,
  allowedHeaders = {"x-requested-with", "origin", "content-type", "accept"},
  origins = "*"
)
@RestController
@RequestMapping("/api/near_earth_object")
@Tag(name = "AstraPortia", description = "sample")
public class NearEarthObjectController {
    
    /** Logger for the class. */
    private static final Logger logger = LoggerFactory.getLogger(NearEarthObjectController.class);
    
    @Autowired
    private AstraStargateApiClient stargateClient;
    
    private String authToken;
    
    public NearEarthObjectController() {}
    
    public NearEarthObjectController(AstraStargateApiClient stargateClient) {
        this.stargateClient = stargateClient;
        Optional<String> token = stargateClient.authentiticate();
        if (token.isPresent()) {
            authToken = token.get();
            logger.info("Your are authenticated to Astra with {}", authToken);
        }
    }
     
    /**
     * Retrieve all tasks (GET)
     */
    @Operation(
            summary = "Retrieve an object",
            description = "retrieve an Object",
            tags = { "near_earth_object" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         description = "Sample",
             content = @Content(schema = @Schema(implementation = NearEarthObject.class))),
            @ApiResponse(responseCode = "400", description = "Title is blank but is mandatory"),
            @ApiResponse(responseCode = "500", description = "An error occur in storage") })
    @RequestMapping(
            value = "/",
            method = GET,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<NearEarthObject> giveMeOneObject(HttpServletRequest request) {
        logger.info("get an object");
        NearEarthObject o1 =  new NearEarthObject();
        return ResponseEntity.ok(o1);
    }

    /**
     * Retrieve all tasks (GET)
     */
    @Operation(
            summary = "Retrieve all the nearest earth",
            description = "retrieve all nearest earth",
            tags = { "near_earth_object" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Sample",
                    content = @Content(schema = @Schema(implementation = NearEarthObject.class))),
            @ApiResponse(responseCode = "400", description = "Title is blank but is mandatory"),
            @ApiResponse(responseCode = "500", description = "An error occur in storage") })
    @RequestMapping(
            value = "/earths",
            method = GET,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NearEarthObject>> findAll(HttpServletRequest request) {
        logger.info("find all NearEarth documents");
        
        List<NearEarthObject> nearEarthObjects = new ArrayList();
        
        NearEarthObject o1 =  new NearEarthObject();
        o1.setDesignation("o1.designation");
        o1.setDiscoveryDate("10/09/2020");
        
        NearEarthObject o2 =  new NearEarthObject();
        o2.setDesignation("o2.designation");
        o2.setDiscoveryDate("21/07/2020");
        
        nearEarthObjects.add(o1);
        nearEarthObjects.add(o2);
        
        return ResponseEntity.ok(nearEarthObjects);
    }

}

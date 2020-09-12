package com.datastax.astraportia.neo;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/earths")
@Tag(name = "AstraPortia", description = "sample")
public class NeoRestController {

    /**
     * Logger for the class.
     */
    private static final Logger logger = LoggerFactory.getLogger(NeoRestController.class);

    /**
     * Retrieve all tasks (GET)
     */
    @Operation(
            summary = "Retrieve an object",
            description = "retrieve an Object",
            tags = {"findById"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Sample",
                    content = @Content(schema = @Schema(implementation = Neo.class))),
            @ApiResponse(responseCode = "400", description = "Title is blank but is mandatory"),
            @ApiResponse(responseCode = "500", description = "An error occur in storage")})
    @RequestMapping(
            value = "/earth/{earthId}",
            method = GET,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Neo> giveMeOneObject(HttpServletRequest request,
                                                           @Parameter(name = "taskId",
                                                                   description = "Unique identifier for earth to be retreived",
                                                                   example = "myEarthId",
                                                                   required = true)
                                                           @PathVariable(value = "earthId") String taskId) {
        logger.info("get an object");
        Neo o1 = new Neo();
        return ResponseEntity.ok(o1);
    }

    /**
     * Retrieve all tasks (GET)
     */
    @Operation(
            summary = "Retrieve all the nearest earth",
            description = "retrieve all nearest earth",
            tags = {"earths"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Sample",
                    content = @Content(schema = @Schema(implementation = Neo.class))),
            @ApiResponse(responseCode = "400", description = "Title is blank but is mandatory"),
            @ApiResponse(responseCode = "500", description = "An error occur in storage")})
    @RequestMapping(
            value = "/",
            method = GET,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Neo>> findAll(HttpServletRequest request) {
        logger.info("find all NearEarth documents");

        List<Neo> nearEarthObjects = new ArrayList<>();

        Neo o1 = new Neo();
        o1.setDesignation("o1.designation");
        o1.setDiscoveryDate("10/09/2020");

        Neo o2 = new Neo();
        o2.setDesignation("o2.designation");
        o2.setDiscoveryDate("21/07/2020");

        nearEarthObjects.add(o1);
        nearEarthObjects.add(o2);

        return ResponseEntity.ok(nearEarthObjects);
    }

    @Operation(
            summary = "Update an existing earth",
            description = "Update a Earth document passing id and providing body",
            tags = {"update"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = Neo.class))),
            @ApiResponse(responseCode = "400", description = "Json body not valid"),
            @ApiResponse(responseCode = "404", description = "Task UUID not found")})
    @RequestMapping(
            value = "/earth/{earthId}",
            method = PATCH,
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Neo> update(HttpServletRequest request,
                                                  @Parameter(name = "earthId",
                                                          required = true,
                                                          description = "Unique identifier for a earth",
                                                          example = "myEarthId")
                                                  @PathVariable(value = "earthId") String earthId,
                                                  @RequestBody
                                                      @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                              description = "Update all fields if needed",
                                                              required = true,
                                                              content = @Content(schema = @Schema(implementation = Neo.class)))
                                                              Neo currentEarth)
            throws URISyntaxException {
        logger.info("update/modify a earth");
        //NearEarthObject currentEarth = new NearEarthObject();

        currentEarth.setOrbitClass(earthId);

        //stargateClient.createDocument(currentEarth,
        //        Optional.ofNullable(earthId),
        //        authToken,
        //        "near_earth_object");


        return ResponseEntity.ok(currentEarth);
    }

    @Operation(
            summary = "create an existing earth",
            description = "Create a Earth document passing id and providing body",
            tags = {"create"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = Neo.class))),
            @ApiResponse(responseCode = "400", description = "Json body not valid"),
            @ApiResponse(responseCode = "404", description = "Task UUID not found")})
    @RequestMapping(
            value = "/earth/{earthId}",
            method = POST,
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Neo> create(HttpServletRequest request,
                                                  @Parameter(name = "earthId",
                                                          required = true,
                                                          description = "Unique identifier for a earth",
                                                          example = "myEarthId")
                                                  @PathVariable(value = "earthId") String earthId,
                                                  @RequestBody
                                                  @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                          description = "create a nearest earth",
                                                          required = true,
                                                          content = @Content(schema = @Schema(implementation = Neo.class)))
                                                          Neo currentEarth)
            throws URISyntaxException {
        logger.info("create a nearest earth");

        currentEarth.setOrbitClass(earthId);

        //stargateClient.create(currentEarth, Optional.ofNullable(earthId), "near_earth_object");


        return ResponseEntity.ok(currentEarth);
    }

    @RequestMapping(value = "/{earthId}", method = DELETE)
    @Operation(summary = "Delete a Earth from its id of exists", description = "Delete a earth from its id of exists")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "No results")})
    public ResponseEntity<Void> delete(HttpServletRequest request,
                                       @Parameter(name = "earthId", required = true,
                                               description = "Unique identifier for the earth",
                                               example = "myEarthId")
                                       @PathVariable(value = "earthId") String earthId) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
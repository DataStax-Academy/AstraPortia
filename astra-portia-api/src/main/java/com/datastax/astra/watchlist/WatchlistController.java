package com.datastax.astra.watchlist;

import com.datastax.astra.client.AstraStargateApiClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;

@CrossOrigin(
        methods = {PUT, POST, GET, OPTIONS, DELETE, PATCH},
        maxAge = 3600,
        allowedHeaders = {"x-requested-with", "origin", "content-type", "accept"},
        origins = "*"
)
@RestController
@RequestMapping("/api/whatchLists")
@Tag(name = "AstraPortia", description = "sample")
public class WatchlistController {

    private static final Logger logger = LoggerFactory.getLogger(WatchlistController.class);

    @Autowired
    private AstraStargateApiClient stargateClient;

    private String authToken;

    public WatchlistController() {}

    public WatchlistController(AstraStargateApiClient stargateClient) {
        this.stargateClient = stargateClient;
        Optional<String> token = stargateClient.authentiticate();
        if (token.isPresent()) {
            authToken = token.get();
            logger.info("Your are authenticated to Astra with {}", authToken);
        }
    }

    /**
     * Retrieve all entries in the watchlist (GET)
     */
    @Operation(
            summary = "Retrieve watchlist",
            description = "Retrieve all entries in the watchlist (GET)",
            tags = { "watchlist" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Sample",
                    content = @Content(schema = @Schema(implementation = Watchlist.class))),
            @ApiResponse(responseCode = "400", description = "TBD"),
            @ApiResponse(responseCode = "500", description = "TBD") })
    @RequestMapping(
            value = "/",
            method = GET,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Watchlist> retrieveFullWatchlist(HttpServletRequest request) {
        logger.info("retrieve watchlist");
        Watchlist w =  new Watchlist();
        return ResponseEntity.ok(w);
    }
}

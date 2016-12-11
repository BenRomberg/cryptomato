package com.benromberg.cryptomato;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class CryptomatoResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(CryptomatoResource.class);
    private static int counter = 0;
    private final TrustedTimestampProvider trustedTimestampProvider;

    @Inject
    public CryptomatoResource(TrustedTimestampProvider trustedTimestampProvider) {
        this.trustedTimestampProvider = trustedTimestampProvider;
    }

    @GET
    @Path("/coldstart")
    public ColdstartResponse getColdstart() {
        counter++;
        LOGGER.info("Counter: {}", counter);
        return new ColdstartResponse(counter);
    }

    @POST
    @Path("/createTimestampToken")
    public TimestampTokenResponse createTimestampToken(TimestampTokenRequest request) throws Exception {
        return trustedTimestampProvider.createTimestampToken(request.getUsername(), request.getPublicKey());
    }
}
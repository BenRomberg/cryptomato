package com.benromberg.cryptomato;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class CryptomatoResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(CryptomatoResource.class);
    private static int counter = 0;

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
        return new TrustedTimestampProvider().createTimestampToken(request.getUsername(), request.getPublicKey());
    }
}
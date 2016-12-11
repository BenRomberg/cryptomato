package com.benromberg.cryptomato;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.*;

public class CryptomatoResourceTest extends JerseyTest {
    private static final String PUBLIC_KEY = "public key";
    private static final String USERNAME = "username";

    @Override
    protected Application configure() {
        return RequestHandler.getResourceConfig();
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(RequestHandler.createJsonProvider());
    }

    @Test
    public void coldstart() throws Exception {
        Response response = target("/coldstart").request().get();
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        ColdstartResponse coldstartResponse = response.readEntity(ColdstartResponse.class);
        assertThat(coldstartResponse.getCounter()).isGreaterThan(0);
    }

    @Test
    public void createTimestampToken() throws Exception {
        Response response = target("/createTimestampToken").request().post(
                Entity.json(new TimestampTokenRequest(USERNAME, PUBLIC_KEY)));
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        TimestampTokenResponse tokenResponse = response.readEntity(TimestampTokenResponse.class);
        assertThat(tokenResponse.getUsername()).isEqualTo(USERNAME);
        assertThat(tokenResponse.getEncodedToken()).isNotEmpty();
    }
}

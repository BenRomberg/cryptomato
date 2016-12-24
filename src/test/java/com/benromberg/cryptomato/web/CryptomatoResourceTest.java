package com.benromberg.cryptomato.web;

import com.benromberg.cryptomato.core.dao.DynamoDbRule;
import com.benromberg.cryptomato.core.dao.UserDao;
import com.benromberg.cryptomato.model.User;
import com.benromberg.cryptomato.web.view.ColdstartResponse;
import com.benromberg.cryptomato.web.view.TimestampTokenRequest;
import com.benromberg.cryptomato.web.view.TimestampTokenResponse;
import com.google.inject.util.Modules;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.*;

public class CryptomatoResourceTest extends JerseyTest {
    private static final String PUBLIC_KEY = "public key";
    private static final String USERNAME = "username";

    @Rule
    public DynamoDbRule dynamoDbRule = new DynamoDbRule();

    private JerseyResourceConfig jerseyResourceConfig;

    @Override
    protected Application configure() {
        jerseyResourceConfig = new JerseyResourceConfig(Modules.override(new GuiceModule()).with(new GuiceTestModule()));
        return jerseyResourceConfig;
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(JerseyResourceConfig.createJsonProvider());
    }

    @Test
    public void coldstart() throws Exception {
        Response response = target("/coldstart").request().get();
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        ColdstartResponse coldstartResponse = response.readEntity(ColdstartResponse.class);
        assertThat(coldstartResponse.getCounter()).isGreaterThan(0);
    }

    @Test
    public void createTimestampToken_ReturnsProperResponse() throws Exception {
        Response response = target("/createTimestampToken").request().post(
                Entity.json(new TimestampTokenRequest(USERNAME, PUBLIC_KEY)));
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        TimestampTokenResponse tokenResponse = response.readEntity(TimestampTokenResponse.class);
        assertThat(tokenResponse.getUsername()).isEqualTo(USERNAME);
        assertThat(tokenResponse.getEncodedToken()).isNotEmpty();
    }

    @Test
    public void createTimestampToken_StoresUser() throws Exception {
        Response response = target("/createTimestampToken").request().post(
                Entity.json(new TimestampTokenRequest(USERNAME, PUBLIC_KEY)));
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        UserDao userDao = jerseyResourceConfig.getInjector().getInstance(UserDao.class);
        assertThat(userDao.findAll()).extracting(User::getName, User::getPublicKey).containsExactly(tuple(USERNAME, PUBLIC_KEY));
    }
}

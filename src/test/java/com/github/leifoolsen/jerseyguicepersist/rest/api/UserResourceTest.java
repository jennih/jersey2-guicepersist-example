package com.github.leifoolsen.jerseyguicepersist.rest.api;

import com.github.leifoolsen.jerseyguicepersist.domain.User;
import com.github.leifoolsen.jerseyguicepersist.main.JettyBootstrap;
import com.github.leifoolsen.jerseyguicepersist.rest.application.ApplicationConfig;
import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class UserResourceTest {
    private static final int PORT = 8080;
    private static final String DEFAULT_CONTEXT_PATH = "/";

    private static Server server;
    private static WebTarget target;

    private static String idU1;
    private static String idU2;

    @BeforeClass
    public static void setUp() throws Exception {
        // Start the server
        server = JettyBootstrap.start(DEFAULT_CONTEXT_PATH, PORT);

        // create the client
        Client c = ClientBuilder.newClient();
        target = c.target(server.getURI()).path(ApplicationConfig.APPLICATION_PATH);

        User u1 = new User("U1", "u1u1", true);
        idU1 = u1.getId();

        User u2 = new User("U2", "u2u2", true);
        idU2 = u2.getId();

        Response response = target
                .path(UserResource.RESOURCE_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(u1, MediaType.APPLICATION_JSON_TYPE));

        response = target
                .path(UserResource.RESOURCE_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(u2, MediaType.APPLICATION_JSON_TYPE));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        JettyBootstrap.stop(server);
    }

    @Test
    public void shouldFindUserByGivenId() {
        final Response response = target
                .path("users")
                .path(idU1)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

        User u = response.readEntity(User.class);
        assertNotNull(u);
        assertThat(u.getId(), equalTo(idU1));
    }

    @Test
    public void unhandeledExceptionShouldReturn_INTERNAL_SERVER_ERROR() {
        final Response response = target
                .path("users")
                .path("unsupported-exception")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus(), equalTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
    }

    @Test
    public void getApplicationWadl() throws Exception {
        final Response response = target
                .path("application.wadl")
                .request(MediaType.APPLICATION_XML)
                .get();

        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
        String wadl = response.readEntity(String.class);
        assertThat(wadl.length(), greaterThan(0));
    }
}
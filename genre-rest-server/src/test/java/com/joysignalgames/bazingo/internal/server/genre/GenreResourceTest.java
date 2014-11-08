package com.joysignalgames.bazingo.internal.server.genre;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static org.junit.Assert.assertEquals;

public class GenreResourceTest {

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() throws Exception {
        server = Main.startServer();

        // Build a client that has hostname verification for SSL disabled.
        // Without this the tests fail due to SSL certification for localhost.
        Client c = ClientBuilder.newBuilder().hostnameVerifier((hostname, session) -> true).build();

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and Main.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());
        target = c.target(Main.BASE_URI);
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void testGET() {
        String responseMsg = target.path("/").request().get(String.class);
        assertEquals("asdf", responseMsg);
    }
}

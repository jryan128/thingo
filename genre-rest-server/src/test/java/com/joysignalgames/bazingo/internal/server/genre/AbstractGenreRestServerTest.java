package com.joysignalgames.bazingo.internal.server.genre;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 * Makes it easy to create a test.
 */
@Ignore
public class AbstractGenreRestServerTest {

    protected HttpServer server;
    protected Client client;

    @Before
    public void setUp() throws Exception {
        setupServerSslSystemProperties();
        server = GenreRestServer.startServer();

        setupClientSslSystemProperties();
        // Build a client that has hostname verification for SSL disabled.
        // Without this the tests fail due to SSL certification for localhost.
        client = ClientBuilder.newBuilder().hostnameVerifier((hostname, session) -> true).build();
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    protected WebTarget newBaseTarget() {
        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and GenreRestServer.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());
        return client.target(GenreRestServer.BASE_URI);
    }

    private static void setupClientSslSystemProperties() {
        System.getProperties().setProperty("javax.net.ssl.trustStore", "src/test/keystore/test.jks");
        System.getProperties().setProperty("javax.net.ssl.trustStorePassword", "password");
    }

    private static void setupServerSslSystemProperties() {
        System.getProperties().setProperty("javax.net.ssl.keyStore", "src/test/keystore/test.jks");
        System.getProperties().setProperty("javax.net.ssl.keyStorePassword", "password");
    }
}
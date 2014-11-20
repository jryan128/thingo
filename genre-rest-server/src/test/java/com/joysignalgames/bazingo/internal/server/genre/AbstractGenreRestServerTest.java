package com.joysignalgames.bazingo.internal.server.genre;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.File;

/**
 * Makes it easy to create a test.
 */
@Ignore
public class AbstractGenreRestServerTest {

    protected HttpServer server;
    protected Client client;
    public static final File TEST_DB_DIR = new File("test-db");

    @Before
    public void setUp() throws Exception {
        setupDbLocationSystemProperty();
        setupServerSslSystemProperties();
        setupClientSslSystemProperties();
        setupServerAndClient();
    }

    private static void setupDbLocationSystemProperty() {
        if (!TEST_DB_DIR.mkdir()) {
            throw new RuntimeException("Could not create test-db directory for test database at " + TEST_DB_DIR.getAbsolutePath());
        }
        System.setProperty(GenreRestServer.DB_LOCATION_PROPERTY, "test-db/test-mapdb");
    }

    private static void setupServerSslSystemProperties() {

        System.setProperty("javax.net.ssl.keyStore", "src/test/keystore/test.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");
    }

    private static void setupClientSslSystemProperties() {
        System.setProperty("javax.net.ssl.trustStore", "src/test/keystore/test.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");
    }

    private void setupServerAndClient() {
        server = GenreRestServer.startServer();

        // Build a client that has hostname verification for SSL disabled.
        // Without this the tests fail due to SSL certification for localhost.
        client = ClientBuilder.newBuilder()
                .hostnameVerifier((hostname, session) -> true)
                .register(JacksonFeature.class)
                .build();
    }

    @After
    public void tearDown() throws Exception {
        if (TEST_DB_DIR.delete()) {
            System.out.println("Could not delete test db directory " + TEST_DB_DIR.getAbsolutePath());
        }
        stopServer();
    }

    private void stopServer() {
        server.shutdown();
    }

    protected void restartServer() {
        stopServer();
        setupServerAndClient();
    }

    protected WebTarget newBaseTarget() {
        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and GenreRestServer.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());
        return client.target(GenreRestServer.BASE_URI);
    }
}

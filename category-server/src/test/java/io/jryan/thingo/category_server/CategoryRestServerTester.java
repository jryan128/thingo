package io.jryan.thingo.category_server;

import java.io.IOException;

/**
 * <p>Runs {@link io.jryan.thingo.category_server.CategoryRestServer}
 * with keystore system properties that point to the test keystore.</p>
 * <p>This class exists so it is easier to run/test the server during development.</p>
 */
public class CategoryRestServerTester {
    public static void main(String args[]) throws IOException {
        // Set server keystore to the test one.
        // WARNING: The test store should NEVER be used in production.
        System.getProperties().setProperty("javax.net.ssl.keyStore", "src/test/keystore/test.jks");
        System.getProperties().setProperty("javax.net.ssl.keyStorePassword", "password");
        CategoryRestServer.main(args);
    }
}

package com.joysignalgames.bazingo.internal.server.genre;

import java.io.IOException;

/**
 * <p>Runs {@link com.joysignalgames.bazingo.internal.server.genre.GenreRestServer}
 * with keystore system properties that point to the test keystore.</p>
 * <p>
 * <p>This class exists so it is easier to run/test the server during development.</p>
 */
public class GenreRestServerTester {
    public static void main(String args[]) throws IOException {
        // Set server keystore to the test one.
        // WARNING: The test store should NEVER be used in production.
        System.getProperties().setProperty("javax.net.ssl.keyStore", "src/test/keystore/test.jks");
        System.getProperties().setProperty("javax.net.ssl.keyStorePassword", "password");
        GenreRestServer.main(args);
    }
}

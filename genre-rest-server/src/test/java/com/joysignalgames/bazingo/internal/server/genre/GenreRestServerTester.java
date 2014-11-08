package com.joysignalgames.bazingo.internal.server.genre;

import java.io.IOException;

public class GenreRestServerTester {
    public static void main(String args[]) throws IOException {
        // Set server keystore to the test one.
        // WARNING: The test store should NEVER be used in production.
        System.getProperties().setProperty("javax.net.ssl.keyStore", "src/test/keystore/test.jks");
        System.getProperties().setProperty("javax.net.ssl.keyStorePassword", "password");
        GenreRestServer.main(args);
    }
}

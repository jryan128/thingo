package com.joysignalgames.bazingo.internal.server.genre;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static final String BASE_URI = "https://localhost:8080/myapp/";

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();

        // Let OS signals (like CTRL-C) clean up the application properly.
        // Mostly so we can easily make it a daemon or a service.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.shutdown();
            }
        });

        // Start the server.
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            server.shutdown();
        }
    }

    public static HttpServer startServer() {
        ResourceConfig rc = new ResourceConfig().packages("com.joysignalgames.bazingo.internal.server.genre");
        SSLContextConfigurator sslCon = setupSslContextConfigurator();
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc, true,
                new SSLEngineConfigurator(sslCon, false, false, false));
    }

    private static SSLContextConfigurator setupSslContextConfigurator() {
        PropertyChecker.validateSystemPropertiesOrDie();
        SSLContextConfigurator sslCon = SSLContextConfigurator.DEFAULT_CONFIG;
        if (!sslCon.validateConfiguration(true)) {
            throw new RuntimeException("The SSL keystore configuration is invalid. Check your provided " +
                    "system properties (-Djavax.net.ssl...) for a bad keystore location and/or password.");
        }
        return sslCon;
    }

    /**
     * Checks if the needed system properties were set.
     */
    private static class PropertyChecker {
        private static void validateSystemPropertiesOrDie() {
            List<String> propertiesNotSet = new ArrayList<>();
            checkProperty("javax.net.ssl.keyStore", propertiesNotSet);
            checkProperty("javax.net.ssl.keyStorePassword", propertiesNotSet);
            if (!propertiesNotSet.isEmpty()) {
                throw new RuntimeException(String.format(
                        "The needed keystore system properties were not set. The properties %s are needed.",
                        propertiesNotSet));
            }
        }

        private static void checkProperty(String propertyKey, List<String> propertiesNotSet) {
            if (!System.getProperties().containsKey(propertyKey)) {
                propertiesNotSet.add(propertyKey);
            }
        }
    }
}


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
    public static final String BASE_URI = "https://localhost:8080/";

    /**
     * Must be run with a Java keystore, via the following system properties:
     * <ul>
     *     <li><tt>javax.net.ssl.keyStore</tt>: the location of the created keystore</li>
     *     <li><tt>javax.net.ssl.keyStorePassword</tt>: its password</li>
     * </ul>
     * Keystores can be created with Java command-line tool <tt>keytool</tt>.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        daemonize(server);
    }

    public static HttpServer startServer() {
        ResourceConfig rc = new ResourceConfig().packages("com.joysignalgames.bazingo.internal.server.genre");
        SSLContextConfigurator sslCon = setupSslContextConfigurator();
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc, true,
                new SSLEngineConfigurator(sslCon, false, false, false));
    }

    private static SSLContextConfigurator setupSslContextConfigurator() {
        validateSSLSystemPropertiesOrDie();
        SSLContextConfigurator sslCon = SSLContextConfigurator.DEFAULT_CONFIG;
        if (!sslCon.validateConfiguration(true)) {
            throw new RuntimeException("The SSL keystore configuration is invalid. Check your provided " +
                    "system properties (-Djavax.net.ssl...) for a bad keystore location and/or password.");
        }
        return sslCon;
    }

    private static void validateSSLSystemPropertiesOrDie() {
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

    private static void daemonize(HttpServer server) {
        addShutdownHookForProperCleanup(server);
        waitUntilServerOrProcessStops(server);
    }

    private static void waitUntilServerOrProcessStops(HttpServer server) {
        // Joins the current thread to just wait until the process
        // is interrupted, has an error, or is shutdown via a signal.
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            server.shutdown();
        }
    }

    private static void addShutdownHookForProperCleanup(final HttpServer server) {
        // Let OS signals (like CTRL-C) clean up the application properly.
        // So we can kind of use it as daemon or a service.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.shutdown();
            }
        });
    }
}


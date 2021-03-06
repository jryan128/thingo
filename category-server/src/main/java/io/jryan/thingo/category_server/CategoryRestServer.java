package io.jryan.thingo.category_server;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>Main class for the rest server that serves up user category info.
 * See {@link #main(String[])} for required system properties.</p>
 * <p>For easy testing and running (NOT IN PRODUCTION) see
 * {@link io.jryan.thingo.category_server.CategoryRestServerTester} in
 * {@code src/test}.</p>
 *
 * @see io.jryan.thingo.category_server.CategoryRestServerTester
 */
public class CategoryRestServer {
    public static final String DB_LOCATION_PROPERTY = "db.location";
    static final URI BASE_URI = UriBuilder.fromUri("http://localhost/").port(8080).build();
    final HttpServer server;

    public CategoryRestServer() {
        validateDbFileProperty();
        server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI,
                new MyResourceConfig(), true,
                new SSLEngineConfigurator(setupSslContextConfigurator(), false, false, false));
        ServerConfiguration serverConfiguration = server.getServerConfiguration();
//        serverConfiguration.setMaxPostSize(2048);
//        serverConfiguration.setMaxFormPostSize(2048);
        serverConfiguration.setMaxRequestParameters(20);
    }

    /**
     * <p>Must be run with a Java keystore, via the following system properties:
     * <ul>
     * <li>{@code javax.net.ssl.keyStore}: the location of the created keystore</li>
     * <li>{@code javax.net.ssl.keyStorePassword}: its password</li>
     * </ul>
     * Keystores can be created with Java command-line tool <tt>keytool</tt>.
     * </p>
     * <p>Can set the mapdb's location with <tt>db.location</tt> (default is current directory).</p>
     *
     * @param args
     */
    public static void main(String[] args) {
        new CategoryRestServer().daemonize();
    }

    private void daemonize() {
        addShutdownHookForProperCleanup();
        waitUntilServerOrProcessStops();
    }

    private static void validateDbFileProperty() {
        String location = System.getProperty(DB_LOCATION_PROPERTY);
        if (location == null) {
            System.setProperty(DB_LOCATION_PROPERTY, "category-rest-server-mapdb");
        }
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

    private void waitUntilServerOrProcessStops() {
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

    private void addShutdownHookForProperCleanup() {
        // Let OS signals (like CTRL-C) clean up the application properly.
        // So we can kind of use it as daemon or a service.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Logger.getLogger(CategoryRestServer.class.getName()).info("Shutting down...");
                server.shutdown();
            }
        });
    }

    private static class MyResourceConfig extends ResourceConfig {
        private MyResourceConfig() {
            register(CategoryResource.class);
            register(new AbstractBinder() {
                @Override
                protected void configure() {
                    // start CategoryService now as singleton
                    bind(new CategoryService()).to(CategoryService.class);
                }
            });
            register(JacksonFeature.class);
        }
    }
}
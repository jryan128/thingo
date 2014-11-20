package com.joysignalgames.bazingo.internal.server.genre.resources;

import com.joysignalgames.bazingo.internal.server.genre.AbstractGenreRestServerTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Simple tests that ensure everything probably works.
 */
public class GenreResourceTest extends AbstractGenreRestServerTest {

    public static String ROM_COM = loadTsv("../genres/Romantic Comedy.tsv");
    public static String SCI_FI = loadTsv("../genres/SciFi.tsv");

    private static String loadTsv(String tsvFilePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(tsvFilePath)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void assertStatusIs(Response.Status status, Response response) {
        assertEquals(status, Response.Status.fromStatusCode(response.getStatus()));
    }

    private static String getIdFromURI(URI uri) {
        String[] split = uri.toString().split("\\?i=");
        if (split.length < 2) {
            throw new RuntimeException(String.format("Could not parse URI for genre ID. [uri: %s]", uri));
        }
        return split[1];
    }

    @Test
    public void testPersistence() throws IOException {
        // Create ROM_COM genre for user 1.
        postNewGenreReturnId("user1", Entity.entity(ROM_COM, MediaType.TEXT_PLAIN_TYPE));

        // Get list of created genres.
        List<String> list = getListOfGenresForUser("user1");

        // Should only be one in the list, get it.
        String data = getGenreData(list.get(0));

        // Compare the data that was stored on the server with the data we have.
        // They should be exactly the same.
        assertEquals(ROM_COM, data);

        // Restart server. This is to see if the changes stay.
        restartServer();

        // Check if the genre is still there under the same ID.
        data = getGenreData(list.get(0));

        // Assert that the data is good again.
        assertEquals(ROM_COM, data);
    }

    private String getGenreData(String id) {
        return getGenreData(newBaseTarget().queryParam("i", id));
    }

    private String getGenreData(URI uri) {
        return getGenreData(client.target(uri));
    }

    private String getGenreData(WebTarget target) {
        return target.request().get(String.class);
    }

    private List<String> getListOfGenresForUser(String user) {
        return newBaseTarget().queryParam("u", user).request().get(new GenericType<List<String>>() {
        });
    }

    @Test
    public void testGetListing() {
        Entity<String> sciFiEntity = Entity.entity(SCI_FI, MediaType.TEXT_PLAIN);
        Entity<String> romComEntity = Entity.entity(ROM_COM, MediaType.TEXT_PLAIN);

        List<String> expected = new ArrayList<>();

        // Create the data that we are going to get a listing of.
        expected.add(postNewGenreReturnId("user1", romComEntity));
        expected.add(postNewGenreReturnId("user1", sciFiEntity));

        // Add another user just to make sure we don't get a list of all the genres.
        postNewGenreReturnId("userWeDontWant", romComEntity);
        postNewGenreReturnId("userWeDontWant2", sciFiEntity);

        // Assert each added one is in the listing from the server.
        List<String> listOfGenresFromServer = getListOfGenresForUser("user1");
        assertEquals("Listings do not match.", new HashSet<>(listOfGenresFromServer), new HashSet<>(expected));
        assertEquals("Lists should be the same size. Duplicates are bad.", expected.size(), listOfGenresFromServer.size());
    }

    private String postNewGenreReturnId(String user, Entity<String> entity) {
        Response r = postNewGenreReturnResponse(user, entity);
        return getIdFromURI(r.getLocation());
    }

    private Response postNewGenreReturnResponse(String user, Entity<String> entity) {
        Response r = newBaseTarget().queryParam("u", user).request().post(entity);
        assertStatusIs(Response.Status.CREATED, r);
        return r;
    }

    @Test
    public void testGetGenreById() {
        // Create new Genre for multiple users.
        Entity<String> sciFiEntity = Entity.entity(SCI_FI, MediaType.TEXT_PLAIN);
        Entity<String> romComEntity = Entity.entity(ROM_COM, MediaType.TEXT_PLAIN);

        String id1 = postNewGenreReturnId("user1", sciFiEntity);
        String id2 = postNewGenreReturnId("user2", romComEntity);

        // Check if we can get it.
        final String message = "Could not get genre by ID for {id=%s";
        assertTrue(String.format(message, id1), getListOfGenresForUser("user1").contains(id1));
        assertTrue(String.format(message, id2), getListOfGenresForUser("user2").contains(id2));
    }

    @Test
    public void testCreateGenre() {
        // Create genre.
        Entity<String> sciFiEntity = Entity.entity(SCI_FI, MediaType.TEXT_PLAIN);
        String id1 = postNewGenreReturnId("user1", sciFiEntity);

        // Make sure it's the right data.
        assertEquals("Data that was return by server is NOT the same as data we gave it.", SCI_FI, getGenreData(id1));
    }

    @Test
    public void testDeleteGenre() {
        // Create genre.
        Entity<String> sciFiEntity = Entity.entity(ROM_COM, MediaType.TEXT_PLAIN);
        Entity<String> romComEntity = Entity.entity(ROM_COM, MediaType.TEXT_PLAIN);
        Response response = postNewGenreReturnResponse("user1", sciFiEntity);
        postNewGenreReturnId("user1", romComEntity);

        // Delete genre.
        client.target(response.getLocation()).queryParam("u", "user1").request().delete();

        // Listing should only have one.
        assertTrue("Genre was not deleted.", getListOfGenresForUser("user1").size() == 1);
    }

    @Test
    public void testUpdateGenre() {
        // Create genre to update.
        Entity<String> romComEntity = Entity.entity(ROM_COM, MediaType.TEXT_PLAIN);
        Response response = postNewGenreReturnResponse("user1", romComEntity);

        // Update with newData.
        Entity<String> newData = Entity.entity("Hey I'm new data!", MediaType.TEXT_PLAIN);
        client.target(response.getLocation()).queryParam("u", "user1").request().put(newData);

        assertEquals("Genre did not update with new data text!", newData.getEntity(), getGenreData(response.getLocation()));
    }
}

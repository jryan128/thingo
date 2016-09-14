package io.jryan.thingo.category_server;

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
// TODO: Maybe migrate over to JerseyTest?
public class CategoryResourceTest extends AbstractCategoryRestServerTest {

    public static String ROM_COM = loadTsv("../categories/Romantic Comedy.tsv");
    public static String SCI_FI = loadTsv("../categories/SciFi.tsv");

    private static String loadTsv(String tsvFilePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(tsvFilePath)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void assertStatusIs(Response.Status status, Response response) {
//        assertEquals("Unexpected HTTP status returned.", status, Response.Status.fromStatusCode(response.getStatus()));
    }

    private static String getIdFromURI(URI uri) {
        String[] split = uri.toString().split("\\?i=");
        if (split.length < 2) {
            throw new RuntimeException(String.format("Could not parse URI for category ID. [uri: %s]", uri));
        }
        return split[1];
    }

    @Test
    public void testPersistence() throws IOException {
        // Create ROM_COM category for user 1.
        postNewCategoryReturnId("user1", Entity.entity(ROM_COM, MediaType.TEXT_PLAIN_TYPE));

        List<String> categoriesForUser1 = getListOfCategoriesForUser("user1");

        assertEquals("There should be only one category for user1", 1, categoriesForUser1.size());
        String storedRomComData = getCategoryData(categoriesForUser1.get(0));

        // The storedRomComData that was stored on the server should be exactly
        // the same as the data we sent.
        assertEquals(ROM_COM, storedRomComData);

        restartServer(); // This is to see if the changes stay after a reset.

        // Check if the category is still there under the same ID.
        storedRomComData = getCategoryData(categoriesForUser1.get(0));
        assertEquals(ROM_COM, storedRomComData);
    }

    private String getCategoryData(String id) {
        return getCategoryData(newBaseTarget().queryParam("i", id));
    }

    private String getCategoryData(URI uri) {
        return getCategoryData(client.target(uri));
    }

    private String getCategoryData(WebTarget target) {
        return target.request().get(String.class);
    }

    private List<String> getListOfCategoriesForUser(String user) {
        return newBaseTarget().queryParam("u", user).request().get(new GenericType<List<String>>() {
        });
    }

    @Test
    public void testGetListing() {
        Entity<String> sciFiEntity = Entity.entity(SCI_FI, MediaType.TEXT_PLAIN);
        Entity<String> romComEntity = Entity.entity(ROM_COM, MediaType.TEXT_PLAIN);

        List<String> expected = new ArrayList<>();

        // Create the data that we are going to get a listing of.
        expected.add(postNewCategoryReturnId("user1", romComEntity));
        expected.add(postNewCategoryReturnId("user1", sciFiEntity));

        // Add another user just to make sure we don't get a list of all the categories.
        postNewCategoryReturnId("userWeDontWant", romComEntity);
        postNewCategoryReturnId("userWeDontWant2", sciFiEntity);

        // Assert each added one is in the listing from the server.
        List<String> listOfCategoriesFromServer = getListOfCategoriesForUser("user1");
        assertEquals("Listings do not match.", new HashSet<>(listOfCategoriesFromServer), new HashSet<>(expected));
        assertEquals("Lists should be the same size. Duplicates are bad.", expected.size(), listOfCategoriesFromServer.size());
    }

    private String postNewCategoryReturnId(String user, Entity<String> entity) {
        Response r = postNewCategoryReturnResponse(user, entity);
        return getIdFromURI(r.getLocation());
    }

    private Response postNewCategoryReturnResponse(String user, Entity<String> entity) {
        Response r = newBaseTarget().queryParam("u", user).request().post(entity);
        assertStatusIs(Response.Status.CREATED, r);
        return r;
    }

    @Test
    public void testGetCategoryById() {
        // Create new Category for multiple users.
        Entity<String> sciFiEntity = Entity.entity(SCI_FI, MediaType.TEXT_PLAIN);
        Entity<String> romComEntity = Entity.entity(ROM_COM, MediaType.TEXT_PLAIN);

        String id1 = postNewCategoryReturnId("user1", sciFiEntity);
        String id2 = postNewCategoryReturnId("user2", romComEntity);

        // Check if we can get it.
        final String message = "Could not get category by ID for {id=%s";
        assertTrue(String.format(message, id1), getListOfCategoriesForUser("user1").contains(id1));
        assertTrue(String.format(message, id2), getListOfCategoriesForUser("user2").contains(id2));
    }

    @Test
    public void testCreateCategory() {
        // Create category.
        Entity<String> sciFiEntity = Entity.entity(SCI_FI, MediaType.TEXT_PLAIN);
        String id1 = postNewCategoryReturnId("user1", sciFiEntity);

        // Make sure it's the right data.
        assertEquals("Data that was return by server is NOT the same as data we gave it.", SCI_FI, getCategoryData(id1));
    }

    @Test
    public void testDeleteCategory() {
        // Create category.
        Entity<String> sciFiEntity = Entity.entity(ROM_COM, MediaType.TEXT_PLAIN);
        Entity<String> romComEntity = Entity.entity(ROM_COM, MediaType.TEXT_PLAIN);
        Response response = postNewCategoryReturnResponse("user1", sciFiEntity);
        postNewCategoryReturnId("user1", romComEntity);

        // Delete category.
        client.target(response.getLocation()).queryParam("u", "user1").request().delete();

        // Listing should only have one.
        assertTrue("Category was not deleted.", getListOfCategoriesForUser("user1").size() == 1);
    }

    @Test
    public void testUpdateCategory() {
        // Create category to update.
        Entity<String> romComEntity = Entity.entity(ROM_COM, MediaType.TEXT_PLAIN);
        Response response = postNewCategoryReturnResponse("user1", romComEntity);

        // Update with newData.
        Entity<String> newData = Entity.entity("Hey I'm new data!", MediaType.TEXT_PLAIN);
        client.target(response.getLocation()).queryParam("u", "user1").request().put(newData);

        assertEquals("Category did not update with new data text!", newData.getEntity(), getCategoryData(response.getLocation()));
    }
}

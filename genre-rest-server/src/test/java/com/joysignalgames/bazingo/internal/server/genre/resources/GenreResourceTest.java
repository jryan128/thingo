package com.joysignalgames.bazingo.internal.server.genre.resources;

import com.joysignalgames.bazingo.internal.server.genre.AbstractGenreRestServerTest;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class GenreResourceTest extends AbstractGenreRestServerTest {

    @Test
    public void testPersistence() throws IOException {
        createUser1AndData();
        restartServer();
    }

    private void createUser1AndData() throws IOException {
        String romCom = new String(Files.readAllBytes(Paths.get("../genres/Romantic Comedy.tsv")), StandardCharsets.UTF_8);
        String sciFi = new String(Files.readAllBytes(Paths.get("../genres/SciFi.tsv")), StandardCharsets.UTF_8);

        Response response = newBaseTarget().queryParam("u", "user1").request()
                .post(Entity.entity(romCom, MediaType.TEXT_PLAIN_TYPE));
        assertStatusIs(Response.Status.CREATED, response);
    }

    private static void assertStatusIs(Response.Status status, Response response) {
        assertEquals(status, Response.Status.fromStatusCode(response.getStatus()));
    }

    @Test
    @Ignore
    public void testListing() {
        String response = newBaseTarget().queryParam("u", "user1").request().get(String.class);
        assertEquals("[1, 2]", response);

        response = newBaseTarget().queryParam("u", "user2").request().get(String.class);
        assertEquals("[be, b]", response);
    }

    @Test
    @Ignore
    public void testGetGenre() {
        String response = newBaseTarget().queryParam("i", "be").request().get(String.class);
        assertNotNull(response);
        assertTrue(response.trim().length() > 0);
        assertTrue(response.contains("Gravaton Surge\t"));
        assertTrue(response.contains("name\t"));

        response = newBaseTarget().queryParam("i", "2").request().get(String.class);
        assertNotNull(response);
        assertTrue(response.trim().length() > 0);
        assertTrue(response.contains("Ridiculous Science Thingy\t<Free Space Description>"));
        assertTrue((response.contains("Interspecies Love\t\nFleeing\t")));
    }

    @Test
    @Ignore
    public void testCreateGenre() {
        createNewGenreForUser1();
    }

    @Test
    @Ignore
    public void testDeleteGenre() {
        URI whereNewGenreIs = createNewGenreForUser1();
        client.target(whereNewGenreIs).queryParam("u", "user1").request().delete();
        String actual = newBaseTarget().queryParam("u", "user1").request().get(String.class);
        assertEquals("[1, 2]", actual);
    }

    @Test
    @Ignore
    public void testUpdateGenre() {
        URI whereNewGenreIs = createNewGenreForUser1();
        String dataExpected = "New DATA IS BETTER DATA RWAR.";
        Entity<String> entity = Entity.entity(dataExpected, MediaType.TEXT_PLAIN);
        client.target(whereNewGenreIs).queryParam("u", "user1").request().put(entity);
        String dataActual = client.target(whereNewGenreIs).request().get(String.class);
        assertEquals(dataExpected, dataActual);
    }

    private URI createNewGenreForUser1() {
        String dataExpected = "Data! Wheee\tdata is data.\nYum\tdata.";
        Entity<String> entity = Entity.entity(dataExpected, MediaType.TEXT_PLAIN);
        Response postResponse = newBaseTarget().queryParam("u", "user1").request().post(entity);
        URI whereNewGenreIs = postResponse.getLocation();
        String dataActual = client.target(whereNewGenreIs).request().get(String.class);
        assertEquals(dataExpected, dataActual);
        String actual = newBaseTarget().queryParam("u", "user1").request().get(String.class);
        assertEquals("[1, 2, bf]", actual);
        return whereNewGenreIs;
    }
}

package com.joysignalgames.bazingo.internal.server.genre.resources;

import com.joysignalgames.bazingo.internal.server.genre.AbstractGenreRestServerTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.URI;

import static org.junit.Assert.*;

public class GenreResourceTest extends AbstractGenreRestServerTest {

    @Test
    public void testListing() {
        String response = newBaseTarget().queryParam("u", "user1").request().get(String.class);
        assertEquals("[1, 2]", response);

        response = newBaseTarget().queryParam("u", "user2").request().get(String.class);
        assertEquals("[be, b]", response);
    }

    @Test
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
    public void testCreateGenre() {
        createNewGenreForUser1();
        String actual = newBaseTarget().queryParam("u", "user1").request().get(String.class);
        assertEquals("[1, 2, bf]", actual);
    }

    private URI createNewGenreForUser1() {
        String dataExpected = "Data! Wheee\tdata is data.\nYum\tdata.";
        Entity<String> entity = Entity.entity(dataExpected, MediaType.TEXT_PLAIN);
        Response postResponse = newBaseTarget().queryParam("u", "user1").request().post(entity);
        URI whereNewGenreIs = postResponse.getLocation();
        String dataActual = client.target(whereNewGenreIs).request().get(String.class);
        assertEquals(dataExpected, dataActual);
        return whereNewGenreIs;
    }
}

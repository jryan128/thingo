package com.joysignalgames.bazingo.internal.server.genre.resources;

import com.joysignalgames.bazingo.internal.server.genre.AbstractGenreRestServerTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GenreResourceTest extends AbstractGenreRestServerTest {

    @Test
    public void testGet() {
        String responseMsg = target.path("/").request().get(String.class);
        assertEquals("asdf", responseMsg);
    }
}

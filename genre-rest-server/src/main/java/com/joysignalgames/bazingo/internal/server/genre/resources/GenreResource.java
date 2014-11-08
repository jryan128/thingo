package com.joysignalgames.bazingo.internal.server.genre.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class GenreResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return "asdf";
    }
}

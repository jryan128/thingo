package com.joysignalgames.bazingo.internal.server.genre.resources;

import com.joysignalgames.bazingo.internal.server.genre.services.GenreService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.TEXT_PLAIN)
public class GenreResource {

    @Inject
    private GenreService genreService;

    @GET
    public Response listGenresOrGetGenre(@QueryParam("u") String user, @QueryParam("i") String genreId) {
        if (user != null && genreId == null) {
            return Response.ok(genreService.getListOfGenresForUser(user).toString()).build();
        } else if (genreId != null && user == null) {
            try {
                return Response.ok(genreService.getGenre(genreId)).build();
            } catch (NumberFormatException e) {
                return makeBadRequestResponse();
            }
        }
        return makeBadRequestResponse();
    }

    @POST
    public Response createGenre(@QueryParam("u") String user, String data) throws URISyntaxException {
        if (user == null || data == null) {
            return makeBadRequestResponse();
        }
        String newId = genreService.createGenre(user, data);
        return Response.created(new URI("/?i=" + newId)).build();
    }

    @PUT
    public Response updateGenre(@QueryParam("i") String genreId, String data) {
        if (genreId == null || data == null) {
            return makeBadRequestResponse();
        }
        genreService.updateGenre(genreId, data);
        return Response.accepted().build();
    }

    @DELETE
    public Response deleteGenre(@QueryParam("u") String user, @QueryParam("i") String genreId) {
        if (user == null || genreId == null) {
            return makeBadRequestResponse();
        }
        genreService.removeGenre(user, genreId);
        return Response.accepted().build();
    }

    private static Response makeBadRequestResponse() {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}

package io.jryan.thingo.category_server;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.TEXT_PLAIN)
public class CategoryResource {

    @Inject
    private CategoryService categoryService;

    private static boolean isAnythingNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                return true;
            }
        }
        return false;
    }

    private static Response makeBadRequestResponse() {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response listCategoriesOrGetCategory(@QueryParam("u") String user, @QueryParam("i") String categoryId) {
        if (user != null && categoryId == null) {
            return Response.ok(categoryService.getListOfCategoriesForUser(user)).build();
        } else if (categoryId != null && user == null) {
            try {
                return Response.ok(categoryService.getCategory(categoryId)).build();
            } catch (NumberFormatException e) {
                return makeBadRequestResponse();
            }
        }
        return makeBadRequestResponse();
    }

    @POST
    public Response createCategory(@QueryParam("u") String user, String data) throws URISyntaxException {
        if (isAnythingNull(user, data)) {
            return makeBadRequestResponse();
        }
        String newId = categoryService.createCategory(user, data);
        return Response.created(new URI("/?i=" + newId)).build();
    }

    @PUT
    public Response updateCategory(@QueryParam("u") String user, @QueryParam("i") String categoryId, String data) {
        if (isAnythingNull(user, categoryId, data)) {
            return makeBadRequestResponse();
        }
        categoryService.updateCategory(user, categoryId, data);
        return Response.noContent().build();
    }

    @DELETE
    public Response deleteCategory(@QueryParam("u") String user, @QueryParam("i") String categoryId) {
        if (isAnythingNull(user, categoryId)) {
            return makeBadRequestResponse();
        }
        categoryService.removeCategory(user, categoryId);
        return Response.noContent().build();
    }
}

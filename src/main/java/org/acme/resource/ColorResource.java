package org.acme.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import io.smallrye.common.annotation.RunOnVirtualThread;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.acme.dto.ColorRequest;
import org.acme.entity.Color;
import org.acme.service.ColorService;

@Path("/colors")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Colors", description = "Color management endpoints")
@RunOnVirtualThread
public class ColorResource {
    @Inject
    ColorService colorService;

    @GET
    @Operation(summary = "List colors")
    @APIResponse(responseCode = "200", description = "Colors list")
    public List<Color> list() {
        return colorService.list();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a color by id")
    @APIResponse(responseCode = "200", description = "Color found")
    @APIResponse(responseCode = "404", description = "Color not found")
    public Response get(@PathParam("id") UUID id) {
        Color color = colorService.findById(id);
        if (color == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(color).build();
    }

    @POST
    @Operation(summary = "Create a color")
    @APIResponse(responseCode = "201", description = "Color created")
    @APIResponse(responseCode = "400", description = "Invalid color payload")
    public Response create(@Valid ColorRequest request, @Context UriInfo uriInfo) {
        Color created;
        try {
            created = colorService.create(toColor(request));
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        URI location = uriInfo.getAbsolutePathBuilder().path(created.id.toString()).build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a color")
    @APIResponse(responseCode = "200", description = "Color updated")
    @APIResponse(responseCode = "400", description = "Invalid color payload")
    @APIResponse(responseCode = "404", description = "Color not found")
    public Response update(@PathParam("id") UUID id, @Valid ColorRequest request) {
        Color updated;
        try {
            updated = colorService.update(id, toColor(request));
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a color")
    @APIResponse(responseCode = "204", description = "Color deleted")
    @APIResponse(responseCode = "404", description = "Color not found")
    public Response delete(@PathParam("id") UUID id) {
        boolean deleted = colorService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    private Color toColor(ColorRequest request) {
        if (request == null) {
            return null;
        }
        Color color = new Color();
        color.name = request.name();
        color.description = request.description();
        color.rgb = request.rgb();
        return color;
    }
}

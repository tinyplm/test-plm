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
import org.acme.entity.Size;
import org.acme.service.SizeService;

@Path("/sizes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Sizes", description = "Size management endpoints")
@RunOnVirtualThread
public class SizeResource {
    @Inject
    SizeService sizeService;

    @GET
    @Operation(summary = "List sizes")
    @APIResponse(responseCode = "200", description = "Sizes list")
    public List<Size> list() {
        return sizeService.list();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a size by id")
    @APIResponse(responseCode = "200", description = "Size found")
    @APIResponse(responseCode = "404", description = "Size not found")
    public Response get(@PathParam("id") UUID id) {
        Size size = sizeService.findById(id);
        if (size == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(size).build();
    }

    @POST
    @Operation(summary = "Create a size")
    @APIResponse(responseCode = "201", description = "Size created")
    @APIResponse(responseCode = "400", description = "Invalid size payload")
    public Response create(@Valid Size size, @Context UriInfo uriInfo) {
        Size created;
        try {
            created = sizeService.create(size);
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        URI location = uriInfo.getAbsolutePathBuilder().path(created.id.toString()).build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a size")
    @APIResponse(responseCode = "200", description = "Size updated")
    @APIResponse(responseCode = "400", description = "Invalid size payload")
    @APIResponse(responseCode = "404", description = "Size not found")
    public Response update(@PathParam("id") UUID id, @Valid Size size) {
        Size updated;
        try {
            updated = sizeService.update(id, size);
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
    @Operation(summary = "Delete a size")
    @APIResponse(responseCode = "204", description = "Size deleted")
    @APIResponse(responseCode = "404", description = "Size not found")
    public Response delete(@PathParam("id") UUID id) {
        boolean deleted = sizeService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}

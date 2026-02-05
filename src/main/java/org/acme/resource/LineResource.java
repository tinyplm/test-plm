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
import org.acme.entity.Line;
import org.acme.service.LineService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/lines")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Lines", description = "Line management endpoints")
@RunOnVirtualThread
public class LineResource {
    @Inject
    LineService lineService;

    @GET
    @Operation(summary = "List lines")
    @APIResponse(responseCode = "200", description = "Lines list")
    public List<Line> list() {
        return lineService.list();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a line by id")
    @APIResponse(responseCode = "200", description = "Line found")
    @APIResponse(responseCode = "404", description = "Line not found")
    public Response get(@PathParam("id") UUID id) {
        Line line = lineService.findById(id);
        if (line == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(line).build();
    }

    @POST
    @Operation(summary = "Create a line")
    @APIResponse(responseCode = "201", description = "Line created")
    @APIResponse(responseCode = "400", description = "Invalid line payload")
    public Response create(@Valid Line line, @Context UriInfo uriInfo) {
        Line created;
        try {
            created = lineService.create(line);
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        URI location = uriInfo.getAbsolutePathBuilder().path(created.id.toString()).build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a line")
    @APIResponse(responseCode = "200", description = "Line updated")
    @APIResponse(responseCode = "400", description = "Invalid line payload")
    @APIResponse(responseCode = "404", description = "Line not found")
    public Response update(@PathParam("id") UUID id, @Valid Line line) {
        Line updated;
        try {
            updated = lineService.update(id, line);
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
    @Operation(summary = "Delete a line")
    @APIResponse(responseCode = "204", description = "Line deleted")
    @APIResponse(responseCode = "404", description = "Line not found")
    public Response delete(@PathParam("id") UUID id) {
        boolean deleted = lineService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}

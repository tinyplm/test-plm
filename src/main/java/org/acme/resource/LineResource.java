package org.acme.resource;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
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
import java.util.UUID;
import org.acme.dto.LineDTO;
import org.acme.entity.Line;
import org.acme.mapper.LineMapper;
import org.acme.service.LineService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

@Path("/lines")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Lines", description = "Line management endpoints")
@RunOnVirtualThread
public class LineResource {

    private static final Logger LOG = Logger.getLogger(LineResource.class);

    @Inject
    LineService lineService;

    @Inject
    LineMapper lineMapper;

    @GET
    @Operation(summary = "List lines")
    @APIResponse(responseCode = "200", description = "Lines list")
    public java.util.List<LineDTO.Response> list() {
        return lineService.list().stream()
                .map(lineMapper::toResponse)
                .toList();
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
        return Response.ok(lineMapper.toResponse(line)).build();
    }

    @POST
    @Operation(summary = "Create a line")
    @APIResponse(responseCode = "201", description = "Line created")
    @APIResponse(responseCode = "400", description = "Invalid line payload")
    public Response create(@Valid LineDTO.Create request, @Context UriInfo uriInfo) {
        LOG.infof("LINE_CREATE_ATTEMPT lineCode=%s", request.lineCode());
        Line created;
        try {
            created = lineService.create(lineMapper.toEntity(request));
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
        URI location = uriInfo.getAbsolutePathBuilder().path(created.id.toString()).build();
        return Response.created(location).entity(lineMapper.toResponse(created)).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a line")
    @APIResponse(responseCode = "200", description = "Line updated")
    @APIResponse(responseCode = "400", description = "Invalid line payload")
    @APIResponse(responseCode = "404", description = "Line not found")
    @APIResponse(responseCode = "409", description = "Optimistic lock failure (version mismatch)")
    public Response update(@PathParam("id") UUID id, @Valid LineDTO.Update request) {
        LOG.infof("LINE_UPDATE_ATTEMPT id=%s", id);
        Line updated;
        try {
            Line updateData = new Line();
            lineMapper.updateEntity(updateData, request);
            
            updated = lineService.update(id, updateData, request.version());
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        } catch (OptimisticLockException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
        
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(lineMapper.toResponse(updated)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a line")
    @APIResponse(responseCode = "204", description = "Line deleted")
    @APIResponse(responseCode = "404", description = "Line not found")
    public Response delete(@PathParam("id") UUID id) {
        LOG.infof("LINE_DELETE_ATTEMPT id=%s", id);
        boolean deleted = lineService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}

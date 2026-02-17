package org.acme.resource;

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
import java.util.List;
import java.util.UUID;
import io.smallrye.common.annotation.RunOnVirtualThread;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.acme.dto.SizeDTO;
import org.acme.entity.Size;
import org.acme.mapper.SizeMapper;
import org.acme.service.SizeService;
import org.jboss.logging.Logger;

@Path("/sizes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Sizes", description = "Size management endpoints")
@RunOnVirtualThread
public class SizeResource {
    
    private static final Logger LOG = Logger.getLogger(SizeResource.class);

    @Inject
    SizeService sizeService;

    @Inject
    SizeMapper sizeMapper;

    @GET
    @Operation(summary = "List sizes")
    @APIResponse(responseCode = "200", description = "Sizes list")
    public List<SizeDTO.Response> list() {
        return sizeService.list().stream()
                .map(sizeMapper::toResponse)
                .toList();
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
        return Response.ok(sizeMapper.toResponse(size)).build();
    }

    @POST
    @Operation(summary = "Create a size")
    @APIResponse(responseCode = "201", description = "Size created")
    @APIResponse(responseCode = "400", description = "Invalid size payload")
    public Response create(@Valid SizeDTO.Create request, @Context UriInfo uriInfo) {
        LOG.infof("SIZE_CREATE_ATTEMPT name=%s", request.name());
        Size created;
        try {
            created = sizeService.create(sizeMapper.toEntity(request));
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
        URI location = uriInfo.getAbsolutePathBuilder().path(created.id.toString()).build();
        return Response.created(location).entity(sizeMapper.toResponse(created)).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a size")
    @APIResponse(responseCode = "200", description = "Size updated")
    @APIResponse(responseCode = "400", description = "Invalid size payload")
    @APIResponse(responseCode = "404", description = "Size not found")
    @APIResponse(responseCode = "409", description = "Optimistic lock failure (version mismatch)")
    public Response update(@PathParam("id") UUID id, @Valid SizeDTO.Update request) {
        LOG.infof("SIZE_UPDATE_ATTEMPT id=%s", id);
        Size updated;
        try {
            Size updateData = new Size();
            sizeMapper.updateEntity(updateData, request);
            
            updated = sizeService.update(id, updateData, request.version());
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        } catch (OptimisticLockException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
        
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(sizeMapper.toResponse(updated)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a size")
    @APIResponse(responseCode = "204", description = "Size deleted")
    @APIResponse(responseCode = "404", description = "Size not found")
    public Response delete(@PathParam("id") UUID id) {
        LOG.infof("SIZE_DELETE_ATTEMPT id=%s", id);
        boolean deleted = sizeService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}

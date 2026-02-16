package org.acme.resource;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.UUID;
import org.acme.dto.ColorDTO;
import org.acme.util.PageResult;
import org.acme.entity.Color;
import org.acme.mapper.ColorMapper;
import org.acme.service.ColorService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/colors")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Colors", description = "Color management endpoints")
@RunOnVirtualThread
public class ColorResource {

    @Inject
    ColorService colorService;

    @Inject
    ColorMapper colorMapper;

    @GET
    @Operation(summary = "List colors (paged)")
    @APIResponse(responseCode = "200", description = "Paged Colors list")
    public PageResult<ColorDTO.Response> list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        if (size > 100) size = 100; // Clamp max size
        PageResult<Color> result = colorService.list(page, size);
        return new PageResult<>(
                result.items().stream().map(colorMapper::toResponse).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
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
        return Response.ok(colorMapper.toResponse(color)).build();
    }

    @POST
    @Operation(summary = "Create a color")
    @APIResponse(responseCode = "201", description = "Color created")
    @APIResponse(responseCode = "400", description = "Invalid color payload")
    public Response create(@Valid ColorDTO.Create request, @Context UriInfo uriInfo) {
        Color created;
        try {
            created = colorService.create(colorMapper.toEntity(request));
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
        URI location = uriInfo.getAbsolutePathBuilder().path(created.id.toString()).build();
        return Response.created(location).entity(colorMapper.toResponse(created)).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a color")
    @APIResponse(responseCode = "200", description = "Color updated")
    @APIResponse(responseCode = "400", description = "Invalid color payload")
    @APIResponse(responseCode = "404", description = "Color not found")
    @APIResponse(responseCode = "409", description = "Optimistic lock failure (version mismatch)")
    public Response update(@PathParam("id") UUID id, @Valid ColorDTO.Update request) {
        Color updated;
        try {
            Color updateData = new Color();
            colorMapper.updateEntity(updateData, request);
            
            updated = colorService.update(id, updateData, request.version());
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        } catch (OptimisticLockException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
        
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(colorMapper.toResponse(updated)).build();
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
}

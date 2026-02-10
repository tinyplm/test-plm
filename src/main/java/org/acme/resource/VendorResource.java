package org.acme.resource;

import io.smallrye.common.annotation.RunOnVirtualThread;
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
import org.acme.entity.Vendor;
import org.acme.service.VendorService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/vendors")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Vendors", description = "Vendor management endpoints")
@RunOnVirtualThread
public class VendorResource {
    @Inject
    VendorService vendorService;

    @GET
    @Operation(summary = "List vendors")
    @APIResponse(responseCode = "200", description = "Vendors list")
    public List<Vendor> list() {
        return vendorService.list();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a vendor by id")
    @APIResponse(responseCode = "200", description = "Vendor found")
    @APIResponse(responseCode = "404", description = "Vendor not found")
    public Response get(@PathParam("id") UUID id) {
        Vendor vendor = vendorService.findById(id);
        if (vendor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(vendor).build();
    }

    @POST
    @Operation(summary = "Create a vendor")
    @APIResponse(responseCode = "201", description = "Vendor created")
    @APIResponse(responseCode = "400", description = "Invalid vendor payload")
    public Response create(@Valid Vendor vendor, @Context UriInfo uriInfo) {
        Vendor created;
        try {
            created = vendorService.create(vendor);
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        URI location = uriInfo.getAbsolutePathBuilder().path(created.id.toString()).build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a vendor")
    @APIResponse(responseCode = "200", description = "Vendor updated")
    @APIResponse(responseCode = "400", description = "Invalid vendor payload")
    @APIResponse(responseCode = "404", description = "Vendor not found")
    public Response update(@PathParam("id") UUID id, @Valid Vendor vendor) {
        Vendor updated;
        try {
            updated = vendorService.update(id, vendor);
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
    @Operation(summary = "Delete a vendor")
    @APIResponse(responseCode = "204", description = "Vendor deleted")
    @APIResponse(responseCode = "404", description = "Vendor not found")
    public Response delete(@PathParam("id") UUID id) {
        boolean deleted = vendorService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}

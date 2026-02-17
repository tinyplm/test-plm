package org.acme.resource;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
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
import java.util.NoSuchElementException;
import java.util.UUID;
import org.acme.dto.ProductVendorSourcingDTO;
import org.acme.entity.ProductVendorSourcing;
import org.acme.mapper.ProductVendorSourcingMapper;
import org.acme.service.ProductVendorSourcingService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

@Path("/products/{productId}/vendors")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Product Vendor Sourcing", description = "Vendor sourcing links for a product")
@RunOnVirtualThread
public class ProductVendorSourcingResource {

    private static final Logger LOG = Logger.getLogger(ProductVendorSourcingResource.class);

    @Inject
    ProductVendorSourcingService productVendorSourcingService;

    @Inject
    ProductVendorSourcingMapper productVendorSourcingMapper;

    @GET
    @Operation(summary = "List vendors linked to a product")
    @APIResponse(responseCode = "200", description = "Linked vendors list")
    @APIResponse(responseCode = "404", description = "Product not found")
    public Response list(@PathParam("productId") UUID productId) {
        try {
            return Response.ok(
                productVendorSourcingService.listByProduct(productId).stream()
                    .map(productVendorSourcingMapper::toResponse)
                    .toList()
            ).build();
        } catch (NoSuchElementException exception) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Operation(summary = "Link a vendor to a product")
    @APIResponse(responseCode = "201", description = "Vendor linked")
    @APIResponse(responseCode = "400", description = "Invalid payload")
    @APIResponse(responseCode = "404", description = "Product or vendor not found")
    public Response create(
            @PathParam("productId") UUID productId,
            @Valid ProductVendorSourcingDTO.Create request,
            @Context UriInfo uriInfo
    ) {
        LOG.infof("PRODUCT_VENDOR_LINK_ATTEMPT vendorId=%s productId=%s", request.vendorId(), productId);
        try {
            ProductVendorSourcing created = productVendorSourcingService.create(productId, productVendorSourcingMapper.toEntity(request));
            URI location = uriInfo.getAbsolutePathBuilder().path(created.id.toString()).build();
            return Response.created(location).entity(productVendorSourcingMapper.toResponse(created)).build();
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        } catch (NoSuchElementException exception) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/{linkId}")
    @Operation(summary = "Update a vendor link for a product")
    @APIResponse(responseCode = "200", description = "Vendor link updated")
    @APIResponse(responseCode = "400", description = "Invalid payload")
    @APIResponse(responseCode = "404", description = "Product, vendor, or link not found")
    @APIResponse(responseCode = "409", description = "Optimistic lock failure")
    public Response update(
            @PathParam("productId") UUID productId,
            @PathParam("linkId") UUID linkId,
            @Valid ProductVendorSourcingDTO.Update request
    ) {
        LOG.infof("PRODUCT_VENDOR_UPDATE_ATTEMPT linkId=%s productId=%s", linkId, productId);
        try {
            ProductVendorSourcing updateData = new ProductVendorSourcing();
            productVendorSourcingMapper.updateEntity(updateData, request);
            
            ProductVendorSourcing updated = productVendorSourcingService.update(productId, linkId, updateData, request.version());
            return Response.ok(productVendorSourcingMapper.toResponse(updated)).build();
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        } catch (NoSuchElementException exception) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (OptimisticLockException exception) {
            return Response.status(Response.Status.CONFLICT).entity(exception.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{linkId}")
    @Operation(summary = "Remove a vendor link from a product")
    @APIResponse(responseCode = "204", description = "Vendor link removed")
    @APIResponse(responseCode = "404", description = "Product vendor link not found")
    public Response delete(@PathParam("productId") UUID productId, @PathParam("linkId") UUID linkId) {
        LOG.infof("PRODUCT_VENDOR_REMOVE_ATTEMPT linkId=%s productId=%s", linkId, productId);
        boolean deleted = productVendorSourcingService.delete(productId, linkId);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}

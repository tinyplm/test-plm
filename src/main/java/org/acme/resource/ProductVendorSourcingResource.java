package org.acme.resource;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import org.acme.entity.ProductVendorSourcing;
import org.acme.service.ProductVendorSourcingCommand;
import org.acme.service.ProductVendorSourcingService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/products/{productId}/vendors")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Product Vendor Sourcing", description = "Vendor sourcing links for a product")
@RunOnVirtualThread
public class ProductVendorSourcingResource {

    @Inject
    ProductVendorSourcingService productVendorSourcingService;

    @GET
    @Operation(summary = "List vendors linked to a product")
    @APIResponse(responseCode = "200", description = "Linked vendors list")
    @APIResponse(responseCode = "404", description = "Product not found")
    public Response list(@PathParam("productId") UUID productId) {
        try {
            return Response.ok(toResponseList(productVendorSourcingService.listByProduct(productId))).build();
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
            @Valid ProductVendorLinkRequest request,
            @Context UriInfo uriInfo
    ) {
        try {
            ProductVendorSourcing created = productVendorSourcingService.create(productId, toCommand(request));
            URI location = uriInfo.getAbsolutePathBuilder().path(created.id.toString()).build();
            return Response.created(location).entity(toResponse(created)).build();
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (NoSuchElementException exception) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Operation(summary = "Replace vendor links for a product")
    @APIResponse(responseCode = "200", description = "Vendor links replaced")
    @APIResponse(responseCode = "400", description = "Invalid payload")
    @APIResponse(responseCode = "404", description = "Product or vendor not found")
    public Response replaceAll(@PathParam("productId") UUID productId, List<@Valid ProductVendorLinkRequest> requests) {
        try {
            if (requests == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            List<ProductVendorSourcing> links = productVendorSourcingService.replaceAll(
                    productId,
                    requests.stream().map(this::toCommand).toList()
            );
            return Response.ok(toResponseList(links)).build();
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (NoSuchElementException exception) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PATCH
    @Path("/{linkId}")
    @Operation(summary = "Update a vendor link for a product")
    @APIResponse(responseCode = "200", description = "Vendor link updated")
    @APIResponse(responseCode = "400", description = "Invalid payload")
    @APIResponse(responseCode = "404", description = "Product, vendor, or link not found")
    public Response update(
            @PathParam("productId") UUID productId,
            @PathParam("linkId") UUID linkId,
            @Valid ProductVendorLinkRequest request
    ) {
        try {
            ProductVendorSourcing updated = productVendorSourcingService.update(productId, linkId, toCommand(request));
            return Response.ok(toResponse(updated)).build();
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (NoSuchElementException exception) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{linkId}")
    @Operation(summary = "Remove a vendor link from a product")
    @APIResponse(responseCode = "204", description = "Vendor link removed")
    @APIResponse(responseCode = "404", description = "Product vendor link not found")
    public Response delete(@PathParam("productId") UUID productId, @PathParam("linkId") UUID linkId) {
        boolean deleted = productVendorSourcingService.delete(productId, linkId);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    private ProductVendorSourcingCommand toCommand(ProductVendorLinkRequest request) {
        return new ProductVendorSourcingCommand(
                request.vendorId,
                request.primaryVendor,
                request.vsn,
                request.factoryName,
                request.factoryCode,
                request.factoryCountry,
                request.sustainable,
                request.contactName,
                request.contactEmail,
                request.contactPhone,
                request.createdBy
        );
    }

    private List<ProductVendorLinkResponse> toResponseList(List<ProductVendorSourcing> links) {
        return links.stream().map(this::toResponse).toList();
    }

    private ProductVendorLinkResponse toResponse(ProductVendorSourcing link) {
        return new ProductVendorLinkResponse(
                link.id,
                link.product.id,
                link.vendor.id,
                link.vendor.name,
                link.primaryVendor,
                link.vsn,
                link.factoryName,
                link.factoryCode,
                link.factoryCountry,
                link.sustainable,
                link.contactName,
                link.contactEmail,
                link.contactPhone,
                link.createdBy,
                link.createdAt,
                link.updatedAt
        );
    }

    public static class ProductVendorLinkRequest {
        @NotNull
        public UUID vendorId;
        public boolean primaryVendor;
        public String vsn;
        public String factoryName;
        public String factoryCode;
        public String factoryCountry;
        public boolean sustainable;
        public String contactName;
        public String contactEmail;
        public String contactPhone;
        public String createdBy;
    }

    public record ProductVendorLinkResponse(
            UUID id,
            UUID productId,
            UUID vendorId,
            String vendorName,
            boolean primaryVendor,
            String vsn,
            String factoryName,
            String factoryCode,
            String factoryCountry,
            boolean sustainable,
            String contactName,
            String contactEmail,
            String contactPhone,
            String createdBy,
            java.time.LocalDateTime createdAt,
            java.time.LocalDateTime updatedAt
    ) {}
}

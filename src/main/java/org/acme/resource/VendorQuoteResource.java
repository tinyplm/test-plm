package org.acme.resource;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.acme.entity.VendorQuote;
import org.acme.entity.VendorQuoteStatus;
import org.acme.service.VendorQuoteCommand;
import org.acme.service.VendorQuoteService;
import org.acme.service.VendorQuoteStatusCommand;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/products/{productId}/vendors/{linkId}/quotes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Vendor Quotes", description = "Vendor costsheets and quote workflows")
@RunOnVirtualThread
public class VendorQuoteResource {

    @Inject
    VendorQuoteService vendorQuoteService;

    @GET
    @Operation(summary = "List vendor quotes for a product vendor link")
    @APIResponse(responseCode = "200", description = "Vendor quotes list")
    @APIResponse(responseCode = "404", description = "Product or vendor link not found")
    public Response list(
            @PathParam("productId") UUID productId,
            @PathParam("linkId") UUID linkId,
            @QueryParam("includeDeleted") @DefaultValue("false") boolean includeDeleted
    ) {
        try {
            return Response.ok(toResponseList(vendorQuoteService.listByLink(productId, linkId, includeDeleted))).build();
        } catch (NoSuchElementException exception) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/{quoteId}")
    @Operation(summary = "Get a vendor quote by id")
    @APIResponse(responseCode = "200", description = "Vendor quote found")
    @APIResponse(responseCode = "404", description = "Product, vendor link, or quote not found")
    public Response get(
            @PathParam("productId") UUID productId,
            @PathParam("linkId") UUID linkId,
            @PathParam("quoteId") UUID quoteId,
            @QueryParam("includeDeleted") @DefaultValue("false") boolean includeDeleted
    ) {
        try {
            return Response.ok(toResponse(vendorQuoteService.findById(productId, linkId, quoteId, includeDeleted))).build();
        } catch (NoSuchElementException exception) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Operation(summary = "Create a vendor quote")
    @APIResponse(responseCode = "201", description = "Vendor quote created")
    @APIResponse(responseCode = "400", description = "Invalid payload")
    @APIResponse(responseCode = "404", description = "Product or vendor link not found")
    public Response create(
            @PathParam("productId") UUID productId,
            @PathParam("linkId") UUID linkId,
            @Valid VendorQuoteRequest request,
            @Context UriInfo uriInfo
    ) {
        try {
            VendorQuote created = vendorQuoteService.create(productId, linkId, toCommand(request));
            URI location = uriInfo.getAbsolutePathBuilder().path(created.id.toString()).build();
            return Response.created(location).entity(toResponse(created)).build();
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (NoSuchElementException exception) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/{quoteId}")
    @Operation(summary = "Update a vendor quote")
    @APIResponse(responseCode = "200", description = "Vendor quote updated")
    @APIResponse(responseCode = "400", description = "Invalid payload")
    @APIResponse(responseCode = "404", description = "Product, vendor link, or quote not found")
    public Response update(
            @PathParam("productId") UUID productId,
            @PathParam("linkId") UUID linkId,
            @PathParam("quoteId") UUID quoteId,
            @Valid VendorQuoteRequest request
    ) {
        try {
            VendorQuote updated = vendorQuoteService.update(productId, linkId, quoteId, toCommand(request));
            return Response.ok(toResponse(updated)).build();
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (NoSuchElementException exception) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PATCH
    @Path("/{quoteId}/status")
    @Operation(summary = "Transition quote workflow status")
    @APIResponse(responseCode = "200", description = "Vendor quote status updated")
    @APIResponse(responseCode = "400", description = "Invalid payload")
    @APIResponse(responseCode = "404", description = "Product, vendor link, or quote not found")
    public Response updateStatus(
            @PathParam("productId") UUID productId,
            @PathParam("linkId") UUID linkId,
            @PathParam("quoteId") UUID quoteId,
            @Valid VendorQuoteStatusRequest request
    ) {
        try {
            VendorQuote updated = vendorQuoteService.updateStatus(productId, linkId, quoteId, toStatusCommand(request));
            return Response.ok(toResponse(updated)).build();
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (NoSuchElementException exception) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{quoteId}")
    @Operation(summary = "Soft delete a vendor quote")
    @APIResponse(responseCode = "204", description = "Vendor quote deleted")
    @APIResponse(responseCode = "404", description = "Product, vendor link, or quote not found")
    public Response delete(
            @PathParam("productId") UUID productId,
            @PathParam("linkId") UUID linkId,
            @PathParam("quoteId") UUID quoteId,
            @QueryParam("deletedBy") String deletedBy
    ) {
        boolean deleted = vendorQuoteService.softDelete(productId, linkId, quoteId, deletedBy);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    private VendorQuoteCommand toCommand(VendorQuoteRequest request) {
        return new VendorQuoteCommand(
                request.quoteNumber,
                request.versionNumber,
                request.currencyCode,
                request.incoterm,
                request.unitCost,
                request.moq,
                request.leadTimeDays,
                request.sampleLeadTimeDays,
                request.materialCost,
                request.laborCost,
                request.overheadCost,
                request.logisticsCost,
                request.dutyCost,
                request.packagingCost,
                request.marginPercent,
                request.totalCost,
                request.capacityPerMonth,
                request.paymentTerms,
                request.validFrom,
                request.validTo,
                request.complianceNotes,
                request.sustainabilityNotes,
                request.createdBy
        );
    }

    private VendorQuoteStatusCommand toStatusCommand(VendorQuoteStatusRequest request) {
        return new VendorQuoteStatusCommand(request.status, request.actor, request.comment);
    }

    private List<VendorQuoteResponse> toResponseList(List<VendorQuote> quotes) {
        return quotes.stream().map(this::toResponse).toList();
    }

    private VendorQuoteResponse toResponse(VendorQuote quote) {
        return new VendorQuoteResponse(
                quote.id,
                quote.productVendorSourcing.product.id,
                quote.productVendorSourcing.id,
                quote.productVendorSourcing.vendor.id,
                quote.productVendorSourcing.vendor.name,
                quote.quoteNumber,
                quote.versionNumber,
                quote.currencyCode,
                quote.incoterm,
                quote.unitCost,
                quote.moq,
                quote.leadTimeDays,
                quote.sampleLeadTimeDays,
                quote.materialCost,
                quote.laborCost,
                quote.overheadCost,
                quote.logisticsCost,
                quote.dutyCost,
                quote.packagingCost,
                quote.marginPercent,
                quote.totalCost,
                quote.capacityPerMonth,
                quote.paymentTerms,
                quote.validFrom,
                quote.validTo,
                quote.complianceNotes,
                quote.sustainabilityNotes,
                quote.status,
                quote.createdBy,
                quote.submittedBy,
                quote.submittedAt,
                quote.reviewedBy,
                quote.reviewedAt,
                quote.approvalComment,
                quote.deleted,
                quote.deletedBy,
                quote.deletedAt,
                quote.createdAt,
                quote.updatedAt
        );
    }

    public static class VendorQuoteRequest {
        @NotNull
        public String quoteNumber;

        @NotNull
        public Integer versionNumber;

        @NotNull
        public String currencyCode;

        public String incoterm;

        @NotNull
        public java.math.BigDecimal unitCost;

        @NotNull
        public Integer moq;

        @NotNull
        public Integer leadTimeDays;

        public Integer sampleLeadTimeDays;
        public java.math.BigDecimal materialCost;
        public java.math.BigDecimal laborCost;
        public java.math.BigDecimal overheadCost;
        public java.math.BigDecimal logisticsCost;
        public java.math.BigDecimal dutyCost;
        public java.math.BigDecimal packagingCost;
        public java.math.BigDecimal marginPercent;
        public java.math.BigDecimal totalCost;
        public Integer capacityPerMonth;
        public String paymentTerms;
        public LocalDate validFrom;
        public LocalDate validTo;
        public String complianceNotes;
        public String sustainabilityNotes;
        public String createdBy;
    }

    public static class VendorQuoteStatusRequest {
        @NotNull
        public VendorQuoteStatus status;
        public String actor;
        public String comment;
    }

    public record VendorQuoteResponse(
            UUID id,
            UUID productId,
            UUID productVendorSourcingId,
            UUID vendorId,
            String vendorName,
            String quoteNumber,
            int versionNumber,
            String currencyCode,
            String incoterm,
            java.math.BigDecimal unitCost,
            int moq,
            int leadTimeDays,
            Integer sampleLeadTimeDays,
            java.math.BigDecimal materialCost,
            java.math.BigDecimal laborCost,
            java.math.BigDecimal overheadCost,
            java.math.BigDecimal logisticsCost,
            java.math.BigDecimal dutyCost,
            java.math.BigDecimal packagingCost,
            java.math.BigDecimal marginPercent,
            java.math.BigDecimal totalCost,
            Integer capacityPerMonth,
            String paymentTerms,
            LocalDate validFrom,
            LocalDate validTo,
            String complianceNotes,
            String sustainabilityNotes,
            VendorQuoteStatus status,
            String createdBy,
            String submittedBy,
            LocalDateTime submittedAt,
            String reviewedBy,
            LocalDateTime reviewedAt,
            String approvalComment,
            boolean deleted,
            String deletedBy,
            LocalDateTime deletedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}

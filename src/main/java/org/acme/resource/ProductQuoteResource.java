package org.acme.resource;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.acme.entity.VendorQuote;
import org.acme.entity.VendorQuoteStatus;
import org.acme.service.VendorQuoteService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/products/{productId}/quotes")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Product Quotes", description = "Cross-vendor quote comparison for a product")
@RunOnVirtualThread
public class ProductQuoteResource {

    @Inject
    VendorQuoteService vendorQuoteService;

    @GET
    @Operation(summary = "List all quotes for a product across vendors")
    @APIResponse(responseCode = "200", description = "Product quotes list")
    @APIResponse(responseCode = "404", description = "Product not found")
    public Response listByProduct(
            @PathParam("productId") UUID productId,
            @QueryParam("includeDeleted") @DefaultValue("false") boolean includeDeleted
    ) {
        try {
            List<VendorQuote> quotes = vendorQuoteService.listByProduct(productId, includeDeleted);
            return Response.ok(toResponseList(quotes)).build();
        } catch (NoSuchElementException exception) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    private List<ProductQuoteResponse> toResponseList(List<VendorQuote> quotes) {
        return quotes.stream().map(this::toResponse).toList();
    }

    private ProductQuoteResponse toResponse(VendorQuote quote) {
        return new ProductQuoteResponse(
                quote.id,
                quote.productVendorSourcing.product.id,
                quote.productVendorSourcing.id,
                quote.productVendorSourcing.vendor.id,
                quote.productVendorSourcing.vendor.name,
                quote.quoteNumber,
                quote.versionNumber,
                quote.currencyCode,
                quote.unitCost,
                quote.moq,
                quote.leadTimeDays,
                quote.totalCost,
                quote.validFrom,
                quote.validTo,
                quote.status,
                quote.deleted,
                quote.createdAt,
                quote.updatedAt
        );
    }

    public record ProductQuoteResponse(
            UUID id,
            UUID productId,
            UUID productVendorSourcingId,
            UUID vendorId,
            String vendorName,
            String quoteNumber,
            int versionNumber,
            String currencyCode,
            java.math.BigDecimal unitCost,
            int moq,
            int leadTimeDays,
            java.math.BigDecimal totalCost,
            LocalDate validFrom,
            LocalDate validTo,
            VendorQuoteStatus status,
            boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}

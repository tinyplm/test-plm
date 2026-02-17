package org.acme.resource;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
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
import java.util.NoSuchElementException;
import java.util.UUID;
import org.acme.dto.VendorQuoteDTO;
import org.acme.entity.VendorQuote;
import org.acme.mapper.VendorQuoteMapper;
import org.acme.service.VendorQuoteService;
import org.acme.service.VendorQuoteStatusCommand;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

@Path("/products/{productId}/vendors/{linkId}/quotes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Vendor Quotes", description = "Vendor costsheets and quote workflows")
@RunOnVirtualThread
public class VendorQuoteResource {

    private static final Logger LOG = Logger.getLogger(VendorQuoteResource.class);

    @Inject
    VendorQuoteService vendorQuoteService;

    @Inject
    VendorQuoteMapper vendorQuoteMapper;

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
            return Response.ok(
                vendorQuoteService.listByLink(productId, linkId, includeDeleted).stream()
                    .map(vendorQuoteMapper::toResponse)
                    .toList()
            ).build();
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
            return Response.ok(vendorQuoteMapper.toResponse(vendorQuoteService.findById(productId, linkId, quoteId, includeDeleted))).build();
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
            @Valid VendorQuoteDTO.Create request,
            @Context UriInfo uriInfo
    ) {
        LOG.infof("VENDOR_QUOTE_CREATE_ATTEMPT quoteNumber=%s linkId=%s", request.quoteNumber(), linkId);
        try {
            VendorQuote created = vendorQuoteService.create(productId, linkId, vendorQuoteMapper.toEntity(request));
            URI location = uriInfo.getAbsolutePathBuilder().path(created.id.toString()).build();
            return Response.created(location).entity(vendorQuoteMapper.toResponse(created)).build();
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
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
    @APIResponse(responseCode = "409", description = "Optimistic lock failure")
    public Response update(
            @PathParam("productId") UUID productId,
            @PathParam("linkId") UUID linkId,
            @PathParam("quoteId") UUID quoteId,
            @Valid VendorQuoteDTO.Update request
    ) {
        LOG.infof("VENDOR_QUOTE_UPDATE_ATTEMPT quoteId=%s linkId=%s", quoteId, linkId);
        try {
            VendorQuote updateData = new VendorQuote();
            vendorQuoteMapper.updateEntity(updateData, request);
            
            VendorQuote updated = vendorQuoteService.update(productId, linkId, quoteId, updateData, request.version());
            return Response.ok(vendorQuoteMapper.toResponse(updated)).build();
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        } catch (NoSuchElementException exception) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (OptimisticLockException exception) {
            return Response.status(Response.Status.CONFLICT).entity(exception.getMessage()).build();
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
            @Valid VendorQuoteDTO.UpdateStatus request
    ) {
        LOG.infof("VENDOR_QUOTE_STATUS_UPDATE_ATTEMPT quoteId=%s status=%s", quoteId, request.status());
        try {
            // Note: Reuse existing command for status for now as it maps cleanly, or create DTO later
            // Assuming VendorQuoteStatusCommand is still used by service for now.
            // Wait, I should probably update service to take DTO fields or keep using Command for status if it's simple.
            // Service uses VendorQuoteStatusCommand. Let's keep it for now as it wasn't requested to change deeply, 
            // but I should map DTO to it.
            VendorQuoteStatusCommand command = new VendorQuoteStatusCommand(request.status(), "API_USER", request.comment()); // Simplification
            
            VendorQuote updated = vendorQuoteService.updateStatus(productId, linkId, quoteId, command);
            return Response.ok(vendorQuoteMapper.toResponse(updated)).build();
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
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
        LOG.infof("VENDOR_QUOTE_DELETE_ATTEMPT quoteId=%s", quoteId);
        boolean deleted = vendorQuoteService.softDelete(productId, linkId, quoteId, deletedBy);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}

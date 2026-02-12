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
import java.util.NoSuchElementException;
import java.util.UUID;
import io.smallrye.common.annotation.RunOnVirtualThread;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.acme.entity.Product;
import org.acme.service.ProductService;

@Path("/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Products", description = "Product management endpoints")
@RunOnVirtualThread
public class ProductResource {
    @Inject
    ProductService productService;

    @GET
    @Operation(summary = "List products")
    @APIResponse(responseCode = "200", description = "Products list")
    public List<Product> list() {
        return productService.list();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a product by id")
    @APIResponse(responseCode = "200", description = "Product found")
    @APIResponse(responseCode = "404", description = "Product not found")
    public Response get(@PathParam("id") UUID id) {
        Product product = productService.findById(id);
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(product).build();
    }

    @POST
    @Operation(summary = "Create a product")
    @APIResponse(responseCode = "201", description = "Product created")
    @APIResponse(responseCode = "400", description = "Invalid product payload")
    public Response create(@Valid Product product, @Context UriInfo uriInfo) {
        Product created;
        try {
            created = productService.create(product);
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        URI location = uriInfo.getAbsolutePathBuilder().path(created.id.toString()).build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a product")
    @APIResponse(responseCode = "200", description = "Product updated")
    @APIResponse(responseCode = "400", description = "Invalid product payload")
    @APIResponse(responseCode = "404", description = "Product not found")
    public Response update(@PathParam("id") UUID id, @Valid Product product) {
        Product updated;
        try {
            updated = productService.update(id, product);
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
    @Operation(summary = "Delete a product")
    @APIResponse(responseCode = "204", description = "Product deleted")
    @APIResponse(responseCode = "404", description = "Product not found")
    public Response delete(@PathParam("id") UUID id) {
        boolean deleted = productService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/image")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Operation(summary = "Upload product image")
    @APIResponse(responseCode = "200", description = "Product image uploaded")
    @APIResponse(responseCode = "400", description = "Invalid image payload or image already exists")
    @APIResponse(responseCode = "404", description = "Product not found")
    public Response addImage(@PathParam("id") UUID id, byte[] imageBytes) {
        try {
            Product product = productService.addImage(id, imageBytes);
            return Response.ok(product).build();
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (NoSuchElementException exception) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/{id}/image")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Operation(summary = "Replace product image")
    @APIResponse(responseCode = "200", description = "Product image replaced")
    @APIResponse(responseCode = "400", description = "Invalid image payload")
    @APIResponse(responseCode = "404", description = "Product not found")
    public Response updateImage(@PathParam("id") UUID id, byte[] imageBytes) {
        try {
            Product product = productService.updateImage(id, imageBytes);
            return Response.ok(product).build();
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (NoSuchElementException exception) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{id}/image")
    @Operation(summary = "Remove product image")
    @APIResponse(responseCode = "204", description = "Product image removed")
    @APIResponse(responseCode = "404", description = "Product or image not found")
    public Response removeImage(@PathParam("id") UUID id) {
        try {
            boolean removed = productService.removeImage(id);
            if (!removed) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.noContent().build();
        } catch (NoSuchElementException exception) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}

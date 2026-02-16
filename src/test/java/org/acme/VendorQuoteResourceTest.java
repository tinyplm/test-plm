package org.acme;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class VendorQuoteResourceTest {

    @Test
    void createUpdateStatusAndSoftDeleteFlow() {
        String lineId = createLine();
        String productId = createProduct(lineId, "Quote Product A");
        String vendorId = createVendor("Vendor Quote A");
        String linkId = createVendorLink(productId, vendorId);

        Map<String, Object> createPayload = quotePayload("Q-100", 1, new BigDecimal("11.2000"), LocalDate.now().plusDays(30));

        Response createResponse = given()
                .contentType(ContentType.JSON)
                .body(createPayload)
                .when().post("/products/{productId}/vendors/{linkId}/quotes", productId, linkId)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("status", equalTo("SUBMITTED"))
                .extract().response();

        String quoteId = createResponse.jsonPath().getString("id");

        given()
                .when().get("/products/{productId}/vendors/{linkId}/quotes", productId, linkId)
                .then()
                .statusCode(200)
                .body("size()", equalTo(1));

        Map<String, Object> updatePayload = quotePayload("Q-100", 1, new BigDecimal("12.3400"), LocalDate.now().plusDays(30));
        updatePayload.put("version", 0);

        given()
                .contentType(ContentType.JSON)
                .body(updatePayload)
                .when().put("/products/{productId}/vendors/{linkId}/quotes/{quoteId}", productId, linkId, quoteId)
                .then()
                .statusCode(200)
                .body("unitCost", equalTo(12.3400f));

        Map<String, Object> reviewStatus = Map.of("status", "UNDER_REVIEW", "actor", "sourcing_user", "comment", "Comparing");
        given()
                .contentType(ContentType.JSON)
                .body(reviewStatus)
                .when().patch("/products/{productId}/vendors/{linkId}/quotes/{quoteId}/status", productId, linkId, quoteId)
                .then()
                .statusCode(200)
                .body("status", equalTo("UNDER_REVIEW"));

        Map<String, Object> approveStatus = Map.of("status", "APPROVED", "actor", "sourcing_user", "comment", "Approved quote");
        given()
                .contentType(ContentType.JSON)
                .body(approveStatus)
                .when().patch("/products/{productId}/vendors/{linkId}/quotes/{quoteId}/status", productId, linkId, quoteId)
                .then()
                .statusCode(200)
                .body("status", equalTo("APPROVED"));

        // Fetch current version to bypass optimistic lock check and verifying business rule
        int currentVersion = given()
            .when().get("/products/{productId}/vendors/{linkId}/quotes/{quoteId}", productId, linkId, quoteId)
            .then().statusCode(200).extract().path("version");

        updatePayload.put("version", currentVersion);

        given()
                .contentType(ContentType.JSON)
                .body(updatePayload)
                .when().put("/products/{productId}/vendors/{linkId}/quotes/{quoteId}", productId, linkId, quoteId)
                .then()
                .statusCode(400);

        given()
                .when().delete("/products/{productId}/vendors/{linkId}/quotes/{quoteId}?deletedBy={deletedBy}", productId, linkId, quoteId, "qa-user")
                .then()
                .statusCode(204);

        given()
                .when().get("/products/{productId}/vendors/{linkId}/quotes/{quoteId}", productId, linkId, quoteId)
                .then()
                .statusCode(404);

        given()
                .when().get("/products/{productId}/vendors/{linkId}/quotes", productId, linkId)
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));

        given()
                .when().get("/products/{productId}/vendors/{linkId}/quotes?includeDeleted=true", productId, linkId)
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].deleted", equalTo(true));
    }

    @Test
    void allowsMultipleApprovedQuotesAcrossVendorsForSameProduct() {
        String lineId = createLine();
        String productId = createProduct(lineId, "Quote Product B");

        String vendorOneId = createVendor("Vendor Quote B1");
        String vendorTwoId = createVendor("Vendor Quote B2");

        String linkOneId = createVendorLink(productId, vendorOneId);
        String linkTwoId = createVendorLink(productId, vendorTwoId);

        String quoteOneId = createQuote(productId, linkOneId, "Q-200", 1, new BigDecimal("9.1000"));
        String quoteTwoId = createQuote(productId, linkTwoId, "Q-300", 1, new BigDecimal("10.2000"));

        transition(productId, linkOneId, quoteOneId, "UNDER_REVIEW");
        transition(productId, linkTwoId, quoteTwoId, "UNDER_REVIEW");

        transition(productId, linkOneId, quoteOneId, "APPROVED");
        transition(productId, linkTwoId, quoteTwoId, "APPROVED");

        given()
                .when().get("/products/{productId}/quotes", productId)
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("status", hasItem("APPROVED"));

        given()
                .when().get("/products/{productId}/vendors/{linkId}/quotes", productId, linkOneId)
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].status", equalTo("APPROVED"));

        given()
                .when().get("/products/{productId}/vendors/{linkId}/quotes", productId, linkTwoId)
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].status", equalTo("APPROVED"));
    }

    @Test
    void validationAndNotFoundCases() {
        String lineId = createLine();
        String productId = createProduct(lineId, "Quote Product C");
        String vendorId = createVendor("Vendor Quote C");
        String linkId = createVendorLink(productId, vendorId);

        given()
                .contentType(ContentType.JSON)
                .body(Map.of())
                .when().post("/products/{productId}/vendors/{linkId}/quotes", productId, linkId)
                .then()
                .statusCode(400);

        UUID missingId = UUID.randomUUID();

        given()
                .when().get("/products/{productId}/vendors/{linkId}/quotes/{quoteId}", productId, linkId, missingId)
                .then()
                .statusCode(404);

        String quoteId = createQuote(
                productId,
                linkId,
                "Q-400",
                1,
                new BigDecimal("8.0000"),
                LocalDate.now().minusDays(1)
        );

        given()
                .contentType(ContentType.JSON)
                .body(Map.of("status", "DRAFT", "actor", "vendor-user"))
                .when().patch("/products/{productId}/vendors/{linkId}/quotes/{quoteId}/status", productId, linkId, quoteId)
                .then()
                .statusCode(400);

        given()
                .contentType(ContentType.JSON)
                .body(Map.of("status", "APPROVED", "actor", "sourcing-user"))
                .when().patch("/products/{productId}/vendors/{linkId}/quotes/{quoteId}/status", productId, linkId, quoteId)
                .then()
                .statusCode(400);
    }

    private void transition(String productId, String linkId, String quoteId, String status) {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("status", status, "actor", "sourcing"))
                .when().patch("/products/{productId}/vendors/{linkId}/quotes/{quoteId}/status", productId, linkId, quoteId)
                .then()
                .statusCode(200)
                .body("status", equalTo(status));
    }

    private String createQuote(String productId, String linkId, String quoteNumber, int versionNumber, BigDecimal unitCost) {
        return createQuote(productId, linkId, quoteNumber, versionNumber, unitCost, LocalDate.now().plusDays(14));
    }

    private String createQuote(
            String productId,
            String linkId,
            String quoteNumber,
            int versionNumber,
            BigDecimal unitCost,
            LocalDate validTo
    ) {
        Map<String, Object> payload = quotePayload(quoteNumber, versionNumber, unitCost, validTo);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().post("/products/{productId}/vendors/{linkId}/quotes", productId, linkId)
                .then()
                .statusCode(201)
                .extract().response();

        return response.jsonPath().getString("id");
    }

    private Map<String, Object> quotePayload(String quoteNumber, int versionNumber, BigDecimal unitCost, LocalDate validTo) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("quoteNumber", quoteNumber);
        payload.put("versionNumber", versionNumber);
        payload.put("currencyCode", "USD");
        payload.put("incoterm", "FOB");
        payload.put("unitCost", unitCost);
        payload.put("moq", 100);
        payload.put("leadTimeDays", 45);
        payload.put("sampleLeadTimeDays", 15);
        payload.put("materialCost", new BigDecimal("3.1000"));
        payload.put("laborCost", new BigDecimal("1.4000"));
        payload.put("overheadCost", new BigDecimal("0.9000"));
        payload.put("logisticsCost", new BigDecimal("0.7000"));
        payload.put("dutyCost", new BigDecimal("0.5000"));
        payload.put("packagingCost", new BigDecimal("0.3000"));
        payload.put("marginPercent", new BigDecimal("15.50"));
        payload.put("totalCost", new BigDecimal("7.9000"));
        payload.put("capacityPerMonth", 25000);
        payload.put("paymentTerms", "Net 45");
        LocalDate validFrom = validTo.isBefore(LocalDate.now()) ? validTo.minusDays(7) : LocalDate.now();
        payload.put("validFrom", validFrom.toString());
        payload.put("validTo", validTo.toString());
        payload.put("complianceNotes", "Factory passed audit.");
        payload.put("sustainabilityNotes", "Recycled packaging.");
        payload.put("createdBy", "vendor-user");
        return payload;
    }

    private String createLine() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("lineCode", "Q-LINE-" + UUID.randomUUID().toString().substring(0, 8));
        payload.put("seasonCode", "ALL");
        payload.put("brandId", UUID.randomUUID().toString());
        payload.put("marketId", UUID.randomUUID().toString());
        payload.put("channelId", UUID.randomUUID().toString());

        Response response = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().post("/lines")
                .then()
                .statusCode(201)
                .extract().response();

        return response.jsonPath().getString("id");
    }

    private String createProduct(String lineId, String name) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", name);
        payload.put("description", "Quote test product");
        payload.put("price", new BigDecimal("20.00"));
        payload.put("quantity", 300);
        payload.put("lineId", lineId);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().post("/products")
                .then()
                .statusCode(201)
                .extract().response();

        return response.jsonPath().getString("id");
    }

    private String createVendor(String name) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", name);
        payload.put("type", "Manufacturer");
        payload.put("status", true);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().post("/vendors")
                .then()
                .statusCode(201)
                .extract().response();

        return response.jsonPath().getString("id");
    }

    private String createVendorLink(String productId, String vendorId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("vendorId", vendorId);
        payload.put("primaryVendor", false);
        payload.put("sustainable", true);
        payload.put("createdBy", "qa-user");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().post("/products/{productId}/vendors", productId)
                .then()
                .statusCode(201)
                .extract().response();

        return response.jsonPath().getString("id");
    }
}

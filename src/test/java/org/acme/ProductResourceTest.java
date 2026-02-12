package org.acme;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ProductResourceTest {

    @Test
    void listReturnsProducts() {
        given()
                .when().get("/products")
                .then()
                .statusCode(200);
    }

    @Test
    void createGetUpdateDeleteHappyPath() {
        String lineId = createLine();

        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Test Widget");
        payload.put("lifecycle", "ACTIVE");
        payload.put("assortment", "CORE");
        payload.put("buyPlan", "Annual Buy Plan");
        payload.put("storeCost", new BigDecimal("6.10"));
        payload.put("retailCost", new BigDecimal("18.50"));
        payload.put("margin", new BigDecimal("67.03"));
        payload.put("buyer", "Alex Buyer");
        payload.put("setWeek", 12);
        payload.put("inspiration", "Street utility trend board.");
        payload.put("price", new BigDecimal("12.34"));
        payload.put("quantity", 5);
        payload.put("line", Map.of("id", lineId));

        Response createResponse = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().post("/products")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Test Widget"))
                .body("lifecycle", equalTo("ACTIVE"))
                .body("assortment", equalTo("CORE"))
                .body("buyPlan", equalTo("Annual Buy Plan"))
                .body("storeCost", equalTo(6.10f))
                .body("retailCost", equalTo(18.50f))
                .body("margin", equalTo(67.03f))
                .body("buyer", equalTo("Alex Buyer"))
                .body("setWeek", equalTo(12))
                .body("inspiration", equalTo("Street utility trend board."))
                .body("quantity", equalTo(5))
                .extract().response();

        String id = createResponse.jsonPath().getString("id");

        given()
                .when().get("/products/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo("Test Widget"))
                .body("lifecycle", equalTo("ACTIVE"))
                .body("assortment", equalTo("CORE"))
                .body("buyPlan", equalTo("Annual Buy Plan"))
                .body("storeCost", equalTo(6.10f))
                .body("retailCost", equalTo(18.50f))
                .body("margin", equalTo(67.03f))
                .body("buyer", equalTo("Alex Buyer"))
                .body("setWeek", equalTo(12))
                .body("inspiration", equalTo("Street utility trend board."))
                .body("quantity", equalTo(5));

        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("name", "Updated Widget");
        updatePayload.put("lifecycle", "PHASE_OUT");
        updatePayload.put("assortment", "SEASONAL");
        updatePayload.put("buyPlan", "Holiday Buy Plan");
        updatePayload.put("storeCost", new BigDecimal("9.50"));
        updatePayload.put("retailCost", new BigDecimal("29.99"));
        updatePayload.put("margin", new BigDecimal("68.32"));
        updatePayload.put("buyer", "Jamie Buyer");
        updatePayload.put("setWeek", 42);
        updatePayload.put("inspiration", "Performance outdoor capsule.");
        updatePayload.put("price", new BigDecimal("99.99"));
        updatePayload.put("quantity", 42);
        updatePayload.put("line", Map.of("id", lineId));

        given()
                .contentType(ContentType.JSON)
                .body(updatePayload)
                .when().put("/products/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo("Updated Widget"))
                .body("lifecycle", equalTo("PHASE_OUT"))
                .body("assortment", equalTo("SEASONAL"))
                .body("buyPlan", equalTo("Holiday Buy Plan"))
                .body("storeCost", equalTo(9.50f))
                .body("retailCost", equalTo(29.99f))
                .body("margin", equalTo(68.32f))
                .body("buyer", equalTo("Jamie Buyer"))
                .body("setWeek", equalTo(42))
                .body("inspiration", equalTo("Performance outdoor capsule."))
                .body("quantity", equalTo(42));

        given()
                .when().delete("/products/{id}", id)
                .then()
                .statusCode(204);

        given()
                .when().get("/products/{id}", id)
                .then()
                .statusCode(404);
    }

    @Test
    void notFoundCases() {
        UUID missingId = UUID.randomUUID();

        given()
                .when().get("/products/{id}", missingId)
                .then()
                .statusCode(404);

        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("name", "Does Not Exist");
        updatePayload.put("price", new BigDecimal("1.23"));
        updatePayload.put("quantity", 1);
        updatePayload.put("line", Map.of("id", createLine()));

        given()
                .contentType(ContentType.JSON)
                .body(updatePayload)
                .when().put("/products/{id}", missingId)
                .then()
                .statusCode(404);

        given()
                .when().delete("/products/{id}", missingId)
                .then()
                .statusCode(404);
    }

    private String createLine() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("lineCode", "TEST");
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
}

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
                .body("quantity", equalTo(5))
                .extract().response();

        String id = createResponse.jsonPath().getString("id");

        given()
                .when().get("/products/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo("Test Widget"))
                .body("quantity", equalTo(5));

        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("name", "Updated Widget");
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

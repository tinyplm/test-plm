package org.acme;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class VendorResourceTest {

    @Test
    void listReturnsVendors() {
        given()
                .when().get("/vendors")
                .then()
                .statusCode(200);
    }

    @Test
    void createGetUpdateDeleteHappyPath() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Test Vendor");
        payload.put("type", "Supplier");
        payload.put("status", true);

        Response createResponse = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().post("/vendors")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Test Vendor"))
                .body("type", equalTo("Supplier"))
                .extract().response();

        String id = createResponse.jsonPath().getString("id");

        given()
                .when().get("/vendors/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo("Test Vendor"));

        Map<String, Object> updatePayload = new HashMap<>(payload);
        updatePayload.put("name", "Updated Vendor");
        updatePayload.put("version", 0);

        given()
                .contentType(ContentType.JSON)
                .body(updatePayload)
                .when().put("/vendors/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo("Updated Vendor"))
                .body("version", equalTo(1));

        given()
                .when().delete("/vendors/{id}", id)
                .then()
                .statusCode(204);

        given()
                .when().get("/vendors/{id}", id)
                .then()
                .statusCode(404);
    }

    @Test
    void optimisticLocking() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Lock Vendor");
        payload.put("status", true);

        Response createResponse = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().post("/vendors")
                .then()
                .statusCode(201)
                .extract().response();

        String id = createResponse.jsonPath().getString("id");

        Map<String, Object> updatePayload = new HashMap<>(payload);
        updatePayload.put("name", "Fail Vendor");
        updatePayload.put("version", 999);

        given()
                .contentType(ContentType.JSON)
                .body(updatePayload)
                .when().put("/vendors/{id}", id)
                .then()
                .statusCode(409);
                
        // Cleanup
        given().when().delete("/vendors/{id}", id).then().statusCode(204);
    }
    
    @Test
    void notFoundCases() {
        UUID missingId = UUID.randomUUID();

        given()
                .when().get("/vendors/{id}", missingId)
                .then()
                .statusCode(404);

        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("name", "Missing");
        updatePayload.put("version", 0);

        given()
                .contentType(ContentType.JSON)
                .body(updatePayload)
                .when().put("/vendors/{id}", missingId)
                .then()
                .statusCode(404);

        given()
                .when().delete("/vendors/{id}", missingId)
                .then()
                .statusCode(404);
    }
}

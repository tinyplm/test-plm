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
class ColorResourceTest {

    @Test
    void listReturnsColors() {
        given()
                .when().get("/colors")
                .then()
                .statusCode(200);
    }

    @Test
    void createGetUpdateDeleteHappyPath() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Test Red");
        payload.put("description", "Test red description.");
        payload.put("rgb", "255,0,0");

        Response createResponse = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().post("/colors")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Test Red"))
                .body("rgb", equalTo("255,0,0"))
                .extract().response();

        String id = createResponse.jsonPath().getString("id");

        given()
                .when().get("/colors/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo("Test Red"))
                .body("rgb", equalTo("255,0,0"));

        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("name", "Updated Red");
        updatePayload.put("description", "Updated red description.");
        updatePayload.put("rgb", "200,0,0");

        given()
                .contentType(ContentType.JSON)
                .body(updatePayload)
                .when().put("/colors/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo("Updated Red"))
                .body("rgb", equalTo("200,0,0"));

        given()
                .when().delete("/colors/{id}", id)
                .then()
                .statusCode(204);

        given()
                .when().get("/colors/{id}", id)
                .then()
                .statusCode(404);
    }

    @Test
    void notFoundCases() {
        UUID missingId = UUID.randomUUID();

        given()
                .when().get("/colors/{id}", missingId)
                .then()
                .statusCode(404);

        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("name", "Missing");
        updatePayload.put("description", "Missing color.");
        updatePayload.put("rgb", "0,0,0");

        given()
                .contentType(ContentType.JSON)
                .body(updatePayload)
                .when().put("/colors/{id}", missingId)
                .then()
                .statusCode(404);

        given()
                .when().delete("/colors/{id}", missingId)
                .then()
                .statusCode(404);
    }
}

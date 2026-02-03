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
class SizeResourceTest {

    @Test
    void listReturnsSizes() {
        given()
                .when().get("/sizes")
                .then()
                .statusCode(200);
    }

    @Test
    void createGetUpdateDeleteHappyPath() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Standard");
        payload.put("sizes", "S,M,L");

        Response createResponse = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().post("/sizes")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Standard"))
                .body("sizes", equalTo("S,M,L"))
                .extract().response();

        String id = createResponse.jsonPath().getString("id");

        given()
                .when().get("/sizes/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo("Standard"))
                .body("sizes", equalTo("S,M,L"));

        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("name", "Updated");
        updatePayload.put("sizes", "XS,S,M");

        given()
                .contentType(ContentType.JSON)
                .body(updatePayload)
                .when().put("/sizes/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo("Updated"))
                .body("sizes", equalTo("XS,S,M"));

        given()
                .when().delete("/sizes/{id}", id)
                .then()
                .statusCode(204);

        given()
                .when().get("/sizes/{id}", id)
                .then()
                .statusCode(404);
    }

    @Test
    void notFoundCases() {
        UUID missingId = UUID.randomUUID();

        given()
                .when().get("/sizes/{id}", missingId)
                .then()
                .statusCode(404);

        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("name", "Missing");
        updatePayload.put("sizes", "M");

        given()
                .contentType(ContentType.JSON)
                .body(updatePayload)
                .when().put("/sizes/{id}", missingId)
                .then()
                .statusCode(404);

        given()
                .when().delete("/sizes/{id}", missingId)
                .then()
                .statusCode(404);
    }
}

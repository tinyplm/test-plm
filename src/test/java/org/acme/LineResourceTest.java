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
class LineResourceTest {

    @Test
    void listReturnsLines() {
        given()
                .when().get("/lines")
                .then()
                .statusCode(200);
    }

    @Test
    void createGetUpdateDeleteHappyPath() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("lineCode", "L001");
        payload.put("seasonCode", "S26");
        payload.put("year", 2026);
        payload.put("brandId", UUID.randomUUID().toString());
        payload.put("marketId", UUID.randomUUID().toString());
        payload.put("channelId", UUID.randomUUID().toString());
        payload.put("plannedStyleCount", 100);
        payload.put("plannedUnits", 5000);
        payload.put("plannedRevenue", 100000.50);

        Response createResponse = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().post("/lines")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("lineCode", equalTo("L001"))
                .body("seasonCode", equalTo("S26"))
                .extract().response();

        String id = createResponse.jsonPath().getString("id");

        given()
                .when().get("/lines/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("lineCode", equalTo("L001"));

        Map<String, Object> updatePayload = new HashMap<>(payload);
        updatePayload.put("lineCode", "L001-UPDATED");
        updatePayload.put("version", 0);

        given()
                .contentType(ContentType.JSON)
                .body(updatePayload)
                .when().put("/lines/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("lineCode", equalTo("L001-UPDATED"))
                .body("version", equalTo(1));

        given()
                .when().delete("/lines/{id}", id)
                .then()
                .statusCode(204);

        given()
                .when().get("/lines/{id}", id)
                .then()
                .statusCode(404);
    }

    @Test
    void notFoundCases() {
        UUID missingId = UUID.randomUUID();

        given()
                .when().get("/lines/{id}", missingId)
                .then()
                .statusCode(404);

        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("lineCode", "Missing");
        updatePayload.put("seasonCode", "Missing");
        updatePayload.put("brandId", UUID.randomUUID().toString());
        updatePayload.put("marketId", UUID.randomUUID().toString());
        updatePayload.put("channelId", UUID.randomUUID().toString());
        updatePayload.put("version", 0);

        given()
                .contentType(ContentType.JSON)
                .body(updatePayload)
                .when().put("/lines/{id}", missingId)
                .then()
                .statusCode(404);

        given()
                .when().delete("/lines/{id}", missingId)
                .then()
                .statusCode(404);
    }
    
    @Test
    void validationError() {
        Map<String, Object> invalidPayload = new HashMap<>();
        // Missing required fields like lineCode, seasonCode, brandId etc.
        
        given()
                .contentType(ContentType.JSON)
                .body(invalidPayload)
                .when().post("/lines")
                .then()
                .statusCode(400);
    }
    
    @Test
    void optimisticLocking() {
         Map<String, Object> payload = new HashMap<>();
        payload.put("lineCode", "LOCK-TEST");
        payload.put("seasonCode", "S26");
        payload.put("year", 2026);
        payload.put("brandId", UUID.randomUUID().toString());
        payload.put("marketId", UUID.randomUUID().toString());
        payload.put("channelId", UUID.randomUUID().toString());

        Response createResponse = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().post("/lines")
                .then()
                .statusCode(201)
                .extract().response();

        String id = createResponse.jsonPath().getString("id");
        
        // Try to update with wrong version
        Map<String, Object> updatePayload = new HashMap<>(payload);
        updatePayload.put("lineCode", "Should Fail");
        updatePayload.put("version", 999);

        given()
                .contentType(ContentType.JSON)
                .body(updatePayload)
                .when().put("/lines/{id}", id)
                .then()
                .statusCode(409);
                
        // Cleanup
        given().when().delete("/lines/{id}", id).then().statusCode(204);
    }
}

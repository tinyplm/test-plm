# Entity Implementation Blueprint

This document outlines the standard pattern for implementing new entities in the **test-plm** project, based on the reference implementation of the `Color` entity and established consistency rules.

Use this guide when adding new domains to ensure consistency across the codebase.

---

## 1. Database Layer (Flyway)

**Reference:** `.gemini/db-rules.md`

1.  **Schema First:** Define the table using a Flyway migration (`src/main/resources/db/migration/V...sql`).
2.  **Standard Columns:** Every table must have `id` (UUID), `created_at`, `updated_at`, `version`, and `deleted`.
3.  **Constraints:** Explicitly name primary keys (`pk_`), foreign keys (`fk_`), and unique constraints (`uk_`).

```sql
-- Example
CREATE TABLE color (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    rgb VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP  NOT NULL,
    updated_at TIMESTAMP  NOT NULL,
    version INTEGER NOT NULL DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_color PRIMARY KEY (id),
    CONSTRAINT uk_color_name UNIQUE (name)
);
```

---

## 2. Entity Layer

**Reference:** `src/main/java/org/acme/entity/Color.java`

1.  **Base Class:** Extend `CoreEntity` (provides ID, audit fields, version, soft delete).
2.  **Panache:** Implement `PanacheEntity.Managed`.
3.  **Annotations:**
    *   `@Entity`
    *   `@Cacheable` (if appropriate)
    *   Validation annotations (`@NotBlank`, etc.) can be placed here for persistence-layer validation, but **DTO validation is mandatory**.
4.  **Fields:** Public fields (Panache style) or private with accessors (Standard JPA). Project prefers public fields for internal entity state access.

```java
@Entity
public class Color extends CoreEntity implements PanacheEntity.Managed {
    @NotBlank
    @Column(nullable = false)
    public String name;
    // ...
}
```

---

## 3. Repository Layer

**Reference:** `src/main/java/org/acme/repository/ColorRepository.java`

1.  **Pattern:** Use the **Repository Pattern**, not Active Record methods on the Entity itself.
2.  **Interface:** Extend `PanacheRepository.Managed<Entity, UUID>`.
3.  **Custom Queries:**
    *   Use `@Find` for simple type-safe queries.
    *   Use `@HQL` for complex queries or specific sorting/filtering.

```java
public interface ColorRepository extends PanacheRepository.Managed<Color, UUID> {
    @Find
    Color findByName(String name);

    @HQL("from Color order by createdAt desc")
    SelectionQuery<Color> findAllQuery();
}
```

---

## 4. DTO Layer (Data Transfer Objects)

**Reference:** `src/main/java/org/acme/dto/ColorDTO.java` & `.gemini/api-consistency-rules.md`

1.  **Separation:** Create explicit Record classes for each operation:
    *   `Create` (Input, no ID/Audit fields)
    *   `Update` (Input, includes `version` for optimistic locking)
    *   `Response` (Output, full representation)
2.  **Validation:** Apply `jakarta.validation` annotations strictly on DTOs.
3.  **Structure:** Use Java Records nested in a parent DTO class.

```java
public class ColorDTO {
    public record Create(@NotBlank String name, ...) {}
    public record Update(@NotBlank String name, long version, ...) {}
    public record Response(UUID id, String name, long version, ...) {}
}
```

---

## 5. Mapper Layer

**Reference:** `src/main/java/org/acme/mapper/ColorMapper.java`

1.  **Role:** Handle conversion between Entity and DTO. Isolate this logic from Service/Resource.
2.  **Scope:** `@ApplicationScoped`.
3.  **Methods:**
    *   `toEntity(Create request)`: Creates new Entity.
    *   `updateEntity(Entity target, Update request)`: Updates existing Entity fields.
    *   `toResponse(Entity source)`: Converts to Response record.

---

## 6. Service Layer

**Reference:** `src/main/java/org/acme/service/ColorService.java`

1.  **Scope:** `@ApplicationScoped`.
2.  **Transactionality:** Use `@Transactional` on state-changing methods (`create`, `update`, `delete`).
3.  **Logic:**
    *   Perform business validation (e.g., duplicates).
    *   Handle **Optimistic Locking**: Check `existing.version != request.version` in `update`.
    *   Return Entities (or Pages of Entities) to the Resource layer.
4.  **Pagination:** Use `Paging.page()` helper.

```java
@Transactional
public Color update(UUID id, Color updateData, long version) {
    Color existing = repository.findById(id);
    if (existing.version != version) {
        throw new OptimisticLockException(...);
    }
    // copy fields
    return existing;
}
```

---

## 7. Resource Layer (API)

**Reference:** `src/main/java/org/acme/resource/ColorResource.java` & `.gemini/api-consistency-rules.md`

1.  **Annotations:**
    *   `@Path`, `@Tag` (OpenAPI)
    *   `@RunOnVirtualThread` (High throughput)
    *   `@Consumes`/`@Produces` (`MediaType.APPLICATION_JSON`)
2.  **Endpoints:** Implement standard CRUD:
    *   `GET / (paged)` -> `PageResult<DTO.Response>`
    *   `GET /{id}` -> `DTO.Response`
    *   `POST /` -> `201 Created` + Location Header
    *   `PUT /{id}` -> `200 OK` (Handle `409 Conflict` for optimistic locking)
    *   `DELETE /{id}` -> `204 No Content`
3.  **Dependency Injection:** Inject `Service` and `Mapper`.
4.  **No Logic:** Keep controllers thin. Delegate to Service.

---

## 8. Testing Layer (Integration Tests)

**Reference:** `src/test/java/org/acme/ColorResourceTest.java`

Tests should be primarily strictly typed integration tests using `RestAssured` and `@QuarkusTest`.

### 8.1 Happy Path Test

Verify the full lifecycle of a resource: Create -> Get -> Update -> Delete.

```java
@Test
void createGetUpdateDeleteHappyPath() {
    // 1. Create
    Map<String, Object> createPayload = Map.of(
        "name", "Test Entity",
        "description", "A valid description"
    );

    Response createResponse = given()
        .contentType(ContentType.JSON)
        .body(createPayload)
        .when().post("/resources")
        .then()
        .statusCode(201)
        .body("id", notNullValue())
        .body("name", equalTo("Test Entity"))
        .extract().response();

    String id = createResponse.jsonPath().getString("id");

    // 2. Get
    given()
        .when().get("/resources/{id}", id)
        .then()
        .statusCode(200)
        .body("id", equalTo(id));

    // 3. Update
    Map<String, Object> updatePayload = Map.of(
        "name", "Updated Entity",
        "version", 0 // Important: Include version for optimistic locking
    );

    given()
        .contentType(ContentType.JSON)
        .body(updatePayload)
        .when().put("/resources/{id}", id)
        .then()
        .statusCode(200)
        .body("name", equalTo("Updated Entity"))
        .body("version", equalTo(1)); // Version should increment

    // 4. Delete
    given()
        .when().delete("/resources/{id}", id)
        .then()
        .statusCode(204);

    // 5. Verify Gone
    given()
        .when().get("/resources/{id}", id)
        .then()
        .statusCode(404);
}
```

### 8.2 Negative Test Cases (Validation & Errors)

Verify that the API correctly rejects invalid data and handles missing resources.

```java
@Test
void validationAndErrorHandling() {
    // 1. Invalid Payload (Validation Error)
    Map<String, Object> invalidPayload = Map.of(
        "name", "" // Empty name (assuming @NotBlank)
    );

    given()
        .contentType(ContentType.JSON)
        .body(invalidPayload)
        .when().post("/resources")
        .then()
        .statusCode(400); // Bad Request

    // 2. Resource Not Found
    UUID missingId = UUID.randomUUID();
    given()
        .when().get("/resources/{id}", missingId)
        .then()
        .statusCode(404);

    // 3. Optimistic Locking Failure (Conflict)
    // Create a resource first...
    // Then try to update with wrong version
    Map<String, Object> conflictPayload = Map.of(
        "name", "Conflict Update",
        "version", 999 // Wrong version
    );

    given()
        .contentType(ContentType.JSON)
        .body(conflictPayload)
        .when().put("/resources/{id}", existingId)
        .then()
        .statusCode(409); // Conflict
}
```

---

## 9. Development Checklist

- [ ] **Flyway:** Migration created and tested.
- [ ] **Entity:** Extends CoreEntity, annotations correct.
- [ ] **Repository:** Interface created.
- [ ] **DTO:** Records defined, Validation added.
- [ ] **Mapper:** Conversions implemented.
- [ ] **Service:** Transactional boundaries, Optimistic Locking check.
- [ ] **Resource:** Virtual Threads, OpenAPI tags, Pagination, Error mapping.
- [ ] **Tests:** Integration tests covering Happy Path and Negative cases.

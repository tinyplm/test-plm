# api-consistency-rules.md

## Purpose

This document defines **mandatory API (Resource layer) conventions** for all REST endpoints in the application.
The objective is to ensure:

* Predictable and uniform API behavior
* Stable client contracts
* Protection of system-managed fields
* Consistent pagination and CRUD semantics
* Reduced ambiguity across services

These rules are **mandatory** for every Resource (REST Controller).

---

# 1. General Principles

1. The API layer represents **business intent**, not database structure.
2. Entities MUST NOT be exposed directly through REST APIs.
3. Request payloads contain only client-owned data.
4. System-managed fields are read-only.
5. All resources must follow identical behavioral patterns.

---

# 2. DTO Separation (Mandatory)

Each resource must define explicit DTOs:

```
<Entity>CreateRequest
<Entity>UpdateRequest
<Entity>Response
```

Example:

```
OrderCreateRequest
OrderUpdateRequest
OrderResponse
```

### Never expose:

* JPA entities
* Panache entities
* Persistence models

---

# 3. Forbidden Fields in Request Payloads

The following fields are **system-controlled** and MUST NOT appear in request payloads:

* `id`
* `version`
* `createdBy`
* `createdOn`
* `updatedBy`
* `updatedOn`

Requests containing these fields must be rejected or ignored.

### Rationale

These values are owned by the application persistence and auditing mechanisms.

---

# 4. Allowed Fields in Response Payloads

Response DTOs MAY include:

* `id`
* `version`
* audit timestamps
* derived or computed fields

Responses represent system state; requests represent user intent.

---

# 5. Standard CRUD Endpoints

Every resource must expose consistent endpoints.

| Operation        | Method       | Endpoint          |
| ---------------- | ------------ | ----------------- |
| Create           | POST         | `/resources`      |
| Get By Id        | GET          | `/resources/{id}` |
| Find All (paged) | GET          | `/resources`      |
| Update           | PUT or PATCH | `/resources/{id}` |
| Delete           | DELETE       | `/resources/{id}` |

---

## 5.1 Create

```
POST /orders
```

Rules:

* Accepts `CreateRequest`
* Returns `201 Created`
* Returns created resource body
* `Location` header recommended

---

## 5.2 Get By Id

```
GET /orders/{id}
```

* Returns `200 OK`
* Returns `404 Not Found` if resource does not exist

---

## 5.3 Find All (Pagination Required)

Unpaged collection endpoints are not allowed.

```
GET /orders?page=0&size=20&sort=createdOn,desc
```

### Default Values

| Parameter | Default |
| --------- | ------- |
| page      | 0       |
| size      | 20      |
| max size  | 100     |

If requested size exceeds maximum, it must be clamped.

---

### Standard Response Shape

```
{
  "items": [...],
  "page": 0,
  "size": 20,
  "totalElements": 125,
  "totalPages": 7
}
```

---

## 5.4 Update

Preferred pattern:

```
PUT /orders/{id}
```

Rules:

* ID comes exclusively from the path
* Request body must NOT contain ID
* Version must be validated for optimistic locking
* Returns updated resource

Version conflict → `409 CONFLICT`.

---

## 5.5 Delete

```
DELETE /orders/{id}
```

Behavior:

* Performs permanent deletion
* Operation is idempotent
* Returns `204 No Content`

---

# 6. Validation Rules

* Use Bean Validation (`jakarta.validation`)
* Apply validation only on request DTOs
* Entities must not contain API validation annotations

Example:

```
@NotBlank
@Size(max = 100)
String name;
```

---

# 7. Error Handling Standard

| Scenario                | Status |
| ----------------------- | ------ |
| Validation failure      | 400    |
| Resource not found      | 404    |
| Optimistic lock failure | 409    |
| Unauthorized            | 401    |
| Forbidden               | 403    |
| Server error            | 500    |

Error response format:

```
{
  "code": "VALIDATION_ERROR",
  "message": "Name is required",
  "details": [...]
}
```

---

# 8. ID Handling

* IDs are generated server-side.
* Clients must never supply IDs.
* Path parameter is the single source of identity.

---

# 9. Version Handling (Optimistic Locking)

* Responses must include `version`.
* Update requests must include `version`.
* ORM optimistic locking enforces concurrency control.
* Version mismatch results in `409 CONFLICT`.

---

# 10. Mapping Rules

Mapping must occur in a dedicated mapper layer.

```
Entity ↔ Mapper ↔ DTO
```

Resource classes must not manually map fields inline.

---

# 11. Resource Class Responsibilities

Resources are responsible only for:

* Accepting requests
* Validating input
* Calling service layer
* Returning responses

Resources must NOT:

* Access repositories directly
* Contain business logic
* Perform persistence operations

---

# 12. Naming Conventions

* Resource paths use plural nouns.
* JSON properties use camelCase.
* Endpoints must be lowercase.

Examples:

```
/orders
/customers
/invoices
```

---

# 13. Consistency Checklist (PR Review)

Before merging:

* [ ] DTO separation implemented
* [ ] No entity exposure
* [ ] No audit fields in requests
* [ ] Pagination implemented (where required)
* [ ] Version included in responses
* [ ] Correct HTTP status codes used
* [ ] Logging implemented for Create/Update/Delete operations using JBoss Logging

---

# 14. Non-Goals

The API layer must not expose:

* database schema
* ORM concepts
* internal lifecycle fields
* persistence timing details

---

# 15. Guiding Rule

If two resources behave differently for the same operation, the API design is incorrect.

Consistency takes precedence over convenience.

---

# 16. Logging Conventions

Every resource MUST perform logging for state-changing operations (Create, Update, Delete) to facilitate auditability and troubleshooting.

*   Use `org.jboss.logging.Logger`.
*   Log at `INFO` level for successful initiations or completions of these operations.
*   Log relevant identifiers (e.g., name for Create, ID for Update/Delete).

Example:
```java
private static final Logger LOG = Logger.getLogger(MyResource.class);

@POST
public Response create(CreateRequest request) {
    LOG.infof("Creating resource: %s", request.name());
    ...
}
```

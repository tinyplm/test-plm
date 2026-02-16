# Gemini Context for test-plm

## Project Overview

**test-plm** is a Product Lifecycle Management (PLM) application built with **Quarkus** (Supersonic Subatomic Java Framework). It manages the lifecycle of products including their design, planning, sourcing (vendors), and quotes.

**Key Technologies:**
*   **Language:** Java 25
*   **Framework:** Quarkus 3.31.3
*   **Database:** PostgreSQL (via JDBC Driver)
*   **ORM:** Hibernate ORM with Panache Next
*   **Migrations:** Flyway
*   **Cloud Storage:** Amazon S3 (via Quarkiverse Amazon Services)
*   **Testing:** JUnit 5/6, REST Assured
*   **Build Tool:** Maven

## Architecture

The application follows a standard layered architecture:

*   **Resources (`src/main/java/org/acme/resource/`)**: REST API endpoints (Jakarta REST/Quarkus REST). Uses `@RunOnVirtualThread` and OpenAPI annotations.
*   **Services (`src/main/java/org/acme/service/`)**: Business logic and transactional boundaries.
*   **Repositories (`src/main/java/org/acme/repository/`)**: Data access layer using **Panache Repository** pattern.
*   **Entities (`src/main/java/org/acme/entity/`)**: JPA entities representing the data model.
*   **DTOs (`src/main/java/org/acme/dto/`)**: Data Transfer Objects, implemented as Java Records.
*   **Mappers (`src/main/java/org/acme/mapper/`)**: (Implicitly mentioned or inferred) for DTO/Entity conversion.

## Building and Running

### Prerequisites
*   Java 25
*   Docker (for DevServices and native builds)

### Key Commands

| Action | Command | Description |
| :--- | :--- | :--- |
| **Dev Mode** | `./mvnw quarkus:dev` | Runs the app with live reload and Dev UI. |
| **Test** | `./mvnw test` | Runs unit/HTTP tests (`*Test.java`). |
| **Integration Test** | `./mvnw verify` | Runs full lifecycle including integration tests (`*IT.java`). |
| **Package** | `./mvnw package` | Builds the standard Quarkus JAR (`target/quarkus-app/`). |
| **Uber JAR** | `./mvnw package -Dquarkus.package.jar.type=uber-jar` | Builds an executable uber JAR. |
| **Native Build** | `./mvnw package -Dnative` | Builds a native executable (requires GraalVM or Docker). |

**Note:** `just` commands are also available as shortcuts (e.g., `just dev`, `just test`).

## Development Conventions

*   **Coding Style:**
    *   Use 4-space indentation.
    *   Package names: lowercase (e.g., `org.acme`).
    *   Class names: PascalCase.
    *   Entity IDs: UUIDs generated with `@UuidGenerator(style = UuidGenerator.Style.TIME)`.
*   **Data Access:**
    *   Use **Panache Repository** pattern over Active Record pattern.
    *   Migrations are managed by **Flyway** in `src/main/resources/db/migration/`.
    *   Never modify old migrations; always create a new version for schema changes.
*   **REST API:**
    *   Use `jakarta.ws.rs` (Jakarta REST) annotations.
    *   Base paths must be plural (e.g., `/products`).
    *   Inject Services into Resources, never Repositories directly.
    *   Document endpoints with OpenAPI annotations (`@Tag`, `@Operation`, `@APIResponse`).
    *   Use `@RunOnVirtualThread` for blocking operations.
*   **Testing:**
    *   Write tests in `src/test/java/`.
    *   Prefer HTTP-level tests with `@QuarkusTest` and **REST Assured**.
    *   Do not mock the database; use Quarkus DevServices (Testcontainers).

## Key Data Models

*   **Product:** Core entity with planning fields (lifecycle, assortment, buyPlan, cost, margin, etc.).
*   **Line:** Grouping for products.
*   **Color / Size:** Reference data.
*   **Vendor:** Suppliers.
*   **ProductVendorSourcing:** Links Products to Vendors.
*   **VendorQuote:** Quotes from vendors for specific product-vendor links.
*   **VendorQuoteStatus:** Status tracking for quotes (Draft, Sent, Received, Accepted, Rejected).

## Available Skills & Workflows

| Skill | Trigger Phrase | Action |
| :--- | :--- | :--- |
| **scaffold-entity** | "create entity" | Creates Panache entity, UUID PK, validation, and Flyway migration. |
| **scaffold-repository-service** | "add repository and service" | Creates Repository and Service for an entity. |
| **scaffold-resource** | "add rest api" | Creates pluralized REST resource with CRUD and OpenAPI docs. |
| **generate-tests** | "add tests" | Generates REST Assured happy-path and error-case tests. |
| **evolve-database** | "update schema" | Creates new Flyway migration and updates entity/DTOs. |

## Instructions on implementation

** You are operating in strict execution mode.

* Your responsibility is to perform only the task explicitly described in my prompt.
* You must NOT expand scope, infer intent, or add improvements beyond what is requested.

** Rules you MUST follow:

* Do exactly what is asked — nothing more.
* Do not refactor unrelated code.
* Do not optimize unless explicitly requested.
* Do not rename variables, files, or structures unless required by the task.
* Do not introduce new libraries, frameworks, patterns, or abstractions.
* No assumptions.

** If information is missing or ambiguous, STOP and ask a clarification question.

* Never guess requirements.
* No unsolicited changes.
* Do not add logging, comments, tests, validations, error handling, formatting changes, or architectural suggestions unless explicitly requested.
* Respect existing structure.
* Preserve coding style, naming, and layout exactly as provided.
* Modify only the minimum necessary lines.

** Output constraints.

* Return only the requested code or result.
* Do not include explanations unless I explicitly ask for them.
* Scope violation policy.
* If you detect opportunities for improvement outside scope, ignore them.
* Do not mention them unless I ask for review or suggestions.

** You are an executor, not a collaborator.
** Wait for explicit instructions before performing any additional action.

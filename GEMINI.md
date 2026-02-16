# Gemini Context for test-plm

## Project Overview

**test-plm** is a Product Lifecycle Management (PLM) application built with **Quarkus** (Supersonic Subatomic Java Framework). It manages the lifecycle of products including their design, planning, sourcing (vendors), and quotes.

**Key Technologies:**
*   **Language:** Java 25
*   **Framework:** Quarkus 3.31.3
*   **Database:** PostgreSQL (with Hibernate ORM Panache Next)
*   **Migrations:** Flyway
*   **Testing:** JUnit 5, REST Assured
*   **Build Tool:** Maven

## Architecture

The application follows a standard layered architecture:

*   **Resources (`src/main/java/org/acme/resource/`)**: REST API endpoints (JAX-RS/RestEasy).
*   **Services (`src/main/java/org/acme/service/`)**: Business logic and transactional boundaries.
*   **Repositories (`src/main/java/org/acme/repository/`)**: Data access layer using Panache Repositories.
*   **Entities (`src/main/java/org/acme/entity/`)**: JPA entities representing the data model (Product, Color, Size, Vendor, etc.).
*   **DTOs (`src/main/java/org/acme/dto/`)**: Data Transfer Objects, preferably implemented as Java Records.

## Building and Running

### Prerequisites
*   Java 25
*   Docker (for DevServices and native builds)

### Key Commands

| Action | Command | Description |
| :--- | :--- | :--- |
| **Dev Mode** | `./mvnw quarkus:dev` | Runs the app with live reload and Dev UI. |
| **Test** | `./mvnw test` | Runs unit tests. |
| **Verify** | `./mvnw verify` | Runs full lifecycle including integration tests. |
| **Package** | `./mvnw package` | Builds the standard Quarkus JAR (`target/quarkus-app/`). |
| **Native Build** | `./mvnw package -Dnative` | Builds a native executable (requires GraalVM or Docker). |

**Note:** You can also use `just` commands if installed (e.g., `just dev`, `just test`).

## Development Conventions

*   **Coding Style:**
    *   Use 4-space indentation.
    *   Package names: lowercase (e.g., `org.acme`).
    *   Class names: PascalCase.
    *   Entity IDs: UUIDs generated with `@UuidGenerator(style = UuidGenerator.Style.TIME)`.
*   **Data Access:**
    *   Use **Panache Repository** pattern (`Repository` classes) over Active Record pattern for this project.
    *   Migrations are managed by **Flyway** in `src/main/resources/db/migration/`.
*   **REST API:**
    *   Use `jakarta.ws.rs` annotations.
    *   Document endpoints with OpenAPI annotations (`@Tag`, `@Operation`).
    *   Use `@RunOnVirtualThread` for blocking operations where appropriate.
*   **Testing:**
    *   Write tests in `src/test/java/`.
    *   Prefer HTTP-level integration tests (`*IT.java` or `*Test.java` with `@QuarkusTest`) to verify behavior.
    *   Do not mock the database; use Testcontainers (DevServices) which Quarkus handles automatically.

## Key Data Models

*   **Product:** Core entity.
*   **Line:** Grouping for products.
*   **Color / Size:** Reference data.
*   **Vendor:** Suppliers.
*   **VendorQuote:** Quotes from vendors for specific products.

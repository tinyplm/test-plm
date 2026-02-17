# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java/org/acme/entity/` contains JPA entities and shared audit infrastructure (`CoreEntity`, `AuditListener`) plus domain entities (`Product`, `Color`, `Size`, `Line`, `Vendor`, `ProductVendorSourcing`, `VendorQuote`, `VendorQuoteStatus`).
- `src/main/java/org/acme/dto/` contains API DTOs (`ProductDTO`, `ColorDTO`, `SizeDTO`, `LineDTO`, `VendorDTO`, `ProductVendorSourcingDTO`, `VendorQuoteDTO`).
- `src/main/java/org/acme/mapper/` contains mapping layer between entities and DTOs.
- `src/main/java/org/acme/repository/` contains Panache Next repositories.
- `src/main/java/org/acme/service/` contains business logic, CRUD orchestration, and object storage integration (`ProductImageStorageService`).
- `src/main/java/org/acme/resource/` contains REST resources.
- `src/main/java/org/acme/util/` contains pagination helpers (`Paging`, `PageResult`).
- `src/main/resources/` holds runtime config (`application.properties`, test auth property files).
- `src/main/resources/db/migration/` contains Flyway migrations (`V1__init_functions.sql`, `V2__init_schema.sql`, `V3__init_data.sql`).
- `src/main/docker/` contains Dockerfiles for JVM, legacy JAR, and native images.
- `.github/workflows/` contains CI workflows.
- `src/test/java/` contains Quarkus + REST Assured tests.
- `target/` is generated build output. Do not edit manually.

## Build, Test, and Development Commands
- `./mvnw quarkus:dev` runs app in dev mode with live reload.
- `./mvnw test` runs `*Test.java` with Surefire.
- `./mvnw verify` runs full lifecycle including integration tests (`*IT.java`) with Failsafe.
- `./mvnw package` builds the standard Quarkus app under `target/quarkus-app/`.
- `./mvnw package -Dquarkus.package.jar.type=uber-jar` builds executable uber JAR.
- `./mvnw package -Dnative` builds a native image (GraalVM required).
- `./mvnw package -Dnative -Dquarkus.native.container-build=true` builds native image in container.
- `just dev`, `just test`, `just package` are shortcuts.
- `just commit` stages all changes, drafts a commit message via codex, then commits.

## Coding Style & Naming Conventions
- Java target release is `25` (`maven.compiler.release=25`).
- Use 4-space indentation and standard Java formatting.
- Package names are lowercase (e.g., `org.acme`); class names use PascalCase.
- UUID IDs are system-generated (no client-provided IDs for create endpoints).
- Keep request contracts DTO-first; do not expose persistence-only fields for create/update inputs.
- JPA uses `jakarta.persistence` annotations with Panache Next.
- Tests follow `*Test.java` (resource/HTTP behavior) and `*IT.java` (integration).

## Testing Guidelines
- Framework: Quarkus JUnit + REST Assured.
- Keep tests in `src/test/java/`.
- Prefer HTTP-level checks: status codes, response shape, and key field behavior.
- Current test coverage includes products, lines, colors, sizes, vendors, and vendor quote flow.
- In environments without Docker/DevServices, `./mvnw test` may fail unless datasource/object storage is configured.

## Commit & Pull Request Guidelines
- Keep commit messages short and imperative (example: `Add color audit fields from identity`).
- PRs should include:
- Summary of changes.
- Test/verification evidence (`./mvnw test`, or compile-only fallback with reason).
- API request/response examples for endpoint contract changes.

## Configuration & Data
- Runtime config: `src/main/resources/application.properties`.
- Flyway runs at startup (`quarkus.flyway.migrate-at-start=true`).
- Schema/data bootstrap is migration-driven via `src/main/resources/db/migration/`.
- Local object storage uses RustFS (S3-compatible), configured via compose and S3 properties.
- Dev auth/testing config may use `src/main/resources/test-users.properties` and `src/main/resources/test-roles.properties`.

## API Notes
- Base CRUD endpoints:
- `/products`
- `/colors`
- `/sizes`
- `/lines`
- `/vendors`
- All expose `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`.

- Product image endpoints (object storage-backed):
- `POST /products/{id}/image` (`application/octet-stream`) add image
- `PUT /products/{id}/image` (`application/octet-stream`) replace image
- `DELETE /products/{id}/image` remove image
- Product query responses expose resolved image URL when reference exists.

- Nested endpoints:
- `/products/{productId}/vendors`: `GET`, `POST`, `PUT` (replace all), `PATCH /{linkId}`, `DELETE /{linkId}`
- `/products/{productId}/vendors/{linkId}/quotes`: `GET`, `POST`, `GET /{quoteId}`, `PUT /{quoteId}`, `PATCH /{quoteId}/status`, `DELETE /{quoteId}` (soft delete)
- `GET /products/{productId}/quotes` for cross-vendor quote comparison.

- Color audit behavior:
- `createdBy` and `updatedBy` are set server-side from authenticated identity.
- These fields are returned in read APIs but are not part of create/update request DTO contract.

- Resources use `@RunOnVirtualThread` and OpenAPI annotations (`@Tag`, `@Operation`, `@APIResponse`).

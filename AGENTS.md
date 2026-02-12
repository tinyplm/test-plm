# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java/org/acme/entity/` contains JPA entities: `Product`, `Color`, `Size`, `Line`, `Vendor`, `ProductVendorSourcing`, `VendorQuote`, `VendorQuoteStatus`.
- `src/main/java/org/acme/repository/` contains Panache Next repositories.
- `src/main/java/org/acme/service/` contains business logic and transactional workflows.
- `src/main/java/org/acme/resource/` contains REST resources.
- `src/main/resources/` holds runtime config, including `application.properties`.
- `src/main/resources/db/migration/` contains Flyway migrations (`V1__init_product_line.sql` through `V6__alter_product_add_planning_fields.sql`).
- `src/main/docker/` contains Dockerfiles for JVM, legacy JAR, and native images.
- `.github/workflows/` contains CI workflows.
- `src/test/java/` contains Quarkus + REST Assured tests.
- `target/` is generated build output. Do not edit manually.

## Build, Test, and Development Commands
- `./mvnw quarkus:dev` runs the app in dev mode with live reload.
- `./mvnw test` runs test classes (`*Test.java`) with Surefire.
- `./mvnw verify` runs full lifecycle including integration tests (`*IT.java`) with Failsafe.
- `./mvnw package` builds the standard Quarkus app under `target/quarkus-app/`.
- `./mvnw package -Dquarkus.package.jar.type=uber-jar` builds an executable uber JAR.
- `./mvnw package -Dnative` builds a native image (GraalVM required).
- `./mvnw package -Dnative -Dquarkus.native.container-build=true` builds native image in a container.
- `just dev`, `just test`, `just package` are shortcuts.
- `just commit` stages changes, drafts a commit message via codex, and commits.

## Coding Style & Naming Conventions
- Java target release is `25` (`maven.compiler.release=25`).
- Use 4-space indentation and standard Java formatting.
- Package names are lowercase (e.g., `org.acme`); class names use PascalCase.
- Entity IDs use UUID generation with `@UuidGenerator(style = UuidGenerator.Style.TIME)`.
- JPA uses `jakarta.persistence` annotations with Panache Next.
- Tests follow `*Test.java` (unit/HTTP-level) and `*IT.java` (integration).

## Testing Guidelines
- Test framework: Quarkus JUnit 5 + REST Assured.
- Keep tests in `src/test/java/`.
- Prefer HTTP-level endpoint tests for behavior and status code validation.
- Existing coverage includes products/colors/sizes plus vendor quote workflow (`VendorQuoteResourceTest`).
- In environments without Docker DevServices, `./mvnw test` may fail unless datasource is configured explicitly.

## Commit & Pull Request Guidelines
- Keep commit messages short and imperative (example: `Add vendor quote status transition checks`).
- PRs should include:
- Summary of changes.
- Test evidence (`./mvnw test` or equivalent).
- API payload/response examples for endpoint changes.

## Configuration & Data
- Runtime config: `src/main/resources/application.properties`.
- Flyway runs at startup (`quarkus.flyway.migrate-at-start=true`).
- Seed/initial data is managed by migrations:
- `V1__init_product_line.sql`
- `V2__init_color.sql`
- `V3__init_size.sql`
- `V4__init_vendor.sql`
- `V5__init_vendor_quote.sql`
- `V6__alter_product_add_planning_fields.sql`

## API Notes
- Base CRUD endpoints:
- `/products`
- `/colors`
- `/sizes`
- `/lines`
- `/vendors`
- All expose `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`.

- Nested endpoints:
- `GET /lines/{id}/products`
- `/products/{productId}/vendors`:
- `GET`, `POST`, `PUT` (replace all), `PATCH /{linkId}`, `DELETE /{linkId}`

- Vendor quote endpoints:
- `/products/{productId}/vendors/{linkId}/quotes`:
- `GET`, `POST`, `GET /{quoteId}`, `PUT /{quoteId}`, `PATCH /{quoteId}/status`, `DELETE /{quoteId}` (soft delete)
- `GET /products/{productId}/quotes` for cross-vendor quote comparison.

- Product planning attributes now include fields such as:
- `lifecycle`, `assortment`, `buyPlan`, `storeCost`, `retailCost`, `margin`, `buyer`, `setWeek`, `inspiration`.

- Resources use `@RunOnVirtualThread` and OpenAPI annotations (`@Tag`, `@Operation`, `@APIResponse`).

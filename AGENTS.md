# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java/` contains application source, split into layered packages:
- `src/main/java/org/acme/entity/` for JPA entities (e.g., `Product`, `Color`, `Size`).
- `src/main/java/org/acme/repository/` for Panache repositories.
- `src/main/java/org/acme/service/` for business logic and persistence workflows.
- `src/main/java/org/acme/resource/` for REST endpoints.
- `src/main/resources/` holds configuration and data files like `application.properties`.
- `src/main/resources/db/migration/` contains Flyway migrations (`V1__create_product.sql`, `V2__seed_products.sql`, `V3__create_color.sql`, `V4__seed_colors.sql`, `V5__create_size.sql`).
- `src/main/docker/` includes Dockerfiles for JVM, legacy JAR, and native images.
- `src/test/java/` contains unit and integration tests (e.g., REST Assured tests for `/products`).
- `target/` is build output (generated artifacts, compiled classes). Do not edit by hand.

## Build, Test, and Development Commands
- `./mvnw quarkus:dev` runs the app in dev mode with live reload.
- `./mvnw test` runs unit tests (`*Test.java`) with Surefire.
- `./mvnw verify` runs unit tests and integration tests (`*IT.java`) with Failsafe.
- `./mvnw package` builds the standard Quarkus JAR at `target/quarkus-app/`.
- `./mvnw package -Dquarkus.package.jar.type=uber-jar` builds a runnable uber JAR.
- `./mvnw package -Dnative` builds a native image (requires GraalVM).
- `./mvnw package -Dnative -Dquarkus.native.container-build=true` builds a native image in a container.

## Coding Style & Naming Conventions
- Java 21 is the target runtime (`maven.compiler.release=21`).
- Use 4-space indentation and standard Java formatting.
- Package names are lowercase (e.g., `org.acme`); class names use PascalCase.
- Tests follow naming patterns: `*Test.java` for unit tests, `*IT.java` for integration tests.
- JPA entities use annotations from `jakarta.persistence` and Panache for persistence.

## Testing Guidelines
- Frameworks: Quarkus JUnit 5 (`quarkus-junit5`) and REST Assured for HTTP-level tests.
- Keep tests in `src/test/java/` and align test class names with the type of test.
- Integration tests are skipped by default unless you run `./mvnw verify` or explicitly enable them.
- For REST tests, prefer exercising endpoints through HTTP (e.g., `GET /products/{id}` returns `404` when missing).

## Commit & Pull Request Guidelines
- Git history is not available in this workspace, so no enforced commit convention was found.
- Recommended: use short, imperative messages (e.g., "Add greeting endpoint"). Include a scope when helpful (e.g., "api: add greeting endpoint").
- Pull requests should include a clear summary, testing notes, and any relevant screenshots for UI changes.

## Configuration & Data
- Runtime config lives in `src/main/resources/application.properties`.
- Flyway is enabled and runs at startup; migrations live in `src/main/resources/db/migration/`.
- Seed data is inserted via Flyway (`V2__seed_products.sql`), not `import.sql`.

## API Notes
- Base paths: `/products`, `/colors`.
- `/products` endpoints: `POST /products`, `GET /products`, `GET /products/{id}`, `PUT /products/{id}`, `DELETE /products/{id}`.
- `/colors` endpoints: `POST /colors`, `GET /colors`, `GET /colors/{id}`, `PUT /colors/{id}`, `DELETE /colors/{id}`.
- Uses JSON payloads; create/update return `400` for invalid bodies and `404` when not found.

# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java/org/acme/entity/` contains JPA entities (`Product`, `Color`, `Size`, `Line`).
- `src/main/java/org/acme/repository/` contains Panache Next repositories.
- `src/main/java/org/acme/service/` contains business logic and persistence workflows.
- `src/main/java/org/acme/resource/` contains REST endpoints.
- `src/main/resources/` holds configuration like `application.properties`.
- `src/main/resources/db/migration/` contains Flyway migrations (`V1__create_product.sql` through `V6__create_line.sql`).
- `src/main/docker/` includes Dockerfiles for JVM, legacy JAR, and native images.
- `.github/workflows/` holds CI workflows.
- `src/test/java/` contains REST Assured tests for `/products`, `/colors`, and `/sizes`.
- `target/` is build output. Do not edit by hand.

## Build, Test, and Development Commands
- `./mvnw quarkus:dev` runs the app in dev mode with live reload.
- `./mvnw test` runs unit tests (`*Test.java`) with Surefire.
- `./mvnw verify` runs unit and integration tests (`*IT.java`) with Failsafe.
- `./mvnw package` builds the standard Quarkus JAR at `target/quarkus-app/`.
- `./mvnw package -Dquarkus.package.jar.type=uber-jar` builds a runnable uber JAR.
- `./mvnw package -Dnative` builds a native image (requires GraalVM).
- `./mvnw package -Dnative -Dquarkus.native.container-build=true` builds a native image in a container.
- `just dev`, `just test`, `just package` provide shortcuts to common tasks.

## Coding Style & Naming Conventions
- Java 25 is the target runtime (`maven.compiler.release=25`).
- Every Entity will uuid as id, the generation will use Hibernate UuidGenerator.Style.TIME to generate UUIDv7, which gets saved in Postgres as UUIDv7
- Use 4-space indentation and standard Java formatting.
- Package names are lowercase (e.g., `org.acme`); class names use PascalCase.
- Tests follow naming patterns: `*Test.java` for unit tests, `*IT.java` for integration tests.
- JPA entities use `jakarta.persistence` annotations and Panache Next.

## Testing Guidelines
- Frameworks: Quarkus JUnit 5 and REST Assured.
- Keep tests in `src/test/java/`.
- Integration tests are skipped by default unless you run `./mvnw verify`.
- Prefer HTTP-level checks for REST tests (e.g., `GET /products/{id}` returns `404` when missing).

## Commit & Pull Request Guidelines
- Git history is not available in this workspace, so no enforced commit convention was found.
- Recommended: short, imperative messages (e.g., `Add line endpoints`).
- PRs should include a summary, testing notes, and screenshots for UI changes.

## Configuration & Data
- Runtime config lives in `src/main/resources/application.properties`.
- Flyway runs at startup; migrations live in `src/main/resources/db/migration/`.
- Seed data is inserted via Flyway (`V2__seed_products.sql`, `V4__seed_colors.sql`).

## API Notes
- Base paths: `/products`, `/colors`, `/sizes`, `/lines`.
- CRUD endpoints exist for each base path: `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`.
- Uses JSON payloads; create/update return `400` for invalid bodies and `404` when not found.
- Resources are annotated with `@RunOnVirtualThread` and OpenAPI `@Tag`.

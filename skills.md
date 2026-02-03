# Skill: scaffold-entity

Trigger phrase:
"scaffold entity" or "create entity"

Steps:
1. Create JPA entity using PanacheEntityBase
2. Use UUID with default value as @UuidGenerator(style = UuidGenerator.Style.TIME) as primary key, refer Product.java
3. Add validation annotations
4. Generate Flyway migration (next version) along with index and load 10 rows of sample data
5. Create repository interface
6. Create service class
7. Do NOT expose REST yet
8. List created files

---

# Skill: scaffold-repository-service

Trigger phrase:
"add repository and service"

Steps:
1. Create PanacheRepository for entity
2. Create Service class
3. Move all persistence logic into service
4. No REST changes
5. List created files

---

# Skill: scaffold-resource

Trigger phrase:
"create resource" or "add rest api"

Steps:
1. Create REST resource class
2. Base path must be plural
3. Add CRUD endpoints
4. Use JSON only
5. Return Response with correct status codes
6. Inject service (never repository)
7. Add OpenAPI annotations
8. List endpoints

---

# Skill: generate-tests

Trigger phrase:
"generate tests" or "add tests"

Steps:
1. Create @QuarkusTest class
2. Test happy path
3. Test not-found cases
4. Use RestAssured
5. Tests must compile
6. List test files

---

# Skill: evolve-database

Trigger phrase:
"evolve database" or "update schema"

Steps:
1. Never modify old migrations
2. Create next Flyway migration
3. Update entity
4. Check nullability
5. Mention backward compatibility risk
6. List migration file

---

# Global Safety Rules

- Never change pom.xml unless explicitly asked
- Never remove code unless asked
- No Lombok
- Java 21
- Quarkus best practices
- Always show file paths

---

# Post-Generation Review

After completing any skill:
1. Re-read code
2. Identify obvious bugs
3. Fix them
4. Summarize changes

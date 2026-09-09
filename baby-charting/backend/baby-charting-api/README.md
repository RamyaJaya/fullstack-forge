# baby-charting-api

Baby charting REST API — a Spring Boot monolith for tracking babies and their care records.

## Security notice

**This API currently has no authentication or authorization.** All endpoints are publicly accessible to anyone who can reach the server. Any client can create, read, update, and delete baby records.

Use this application **only for local development and learning** until authentication (Phase 7) and baby-scoped RBAC (Phase 8) are implemented. Do not expose it to a network or deploy it to a shared environment in its current state.

## Prerequisites

- **JDK 17+** (as configured in `pom.xml`)
- **Maven**
- **PostgreSQL** running locally with database `babycharting`

Update `src/main/resources/application-local.yml` if your local PostgreSQL credentials differ from the defaults (`dev` / `dev` on `localhost:5432`).

## Local setup

1. Ensure PostgreSQL is running and the `babycharting` database exists.

2. Run the application with the `local` profile:

   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

3. Verify the health endpoint:

   ```bash
   curl http://localhost:8080/actuator/health
   ```

   Expected: `"status":"UP"` with database connectivity reported as UP.

4. Open Swagger UI:

   ```
   http://localhost:8080/swagger-ui/index.html
   ```

## Maven commands

| Task | Command |
|------|---------|
| Run (local profile) | `mvn spring-boot:run -Dspring-boot.run.profiles=local` |
| Run tests | `mvn test` |
| Package JAR | `mvn package` |
| Clean and verify | `mvn clean verify` |
| Run packaged JAR | `java -jar target/baby-charting-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=local` |

Tests require your local PostgreSQL instance to be running and reachable using the `test` profile settings in `src/test/resources/application-test.yml`.

## API documentation

| Resource | URL |
|----------|-----|
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |

## Baby CRUD API

Base path: `/api/v1/babies`

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/babies` | Create a baby |
| `GET` | `/api/v1/babies` | List babies (paginated) |
| `GET` | `/api/v1/babies/{id}` | Get a baby by id |
| `PATCH` | `/api/v1/babies/{id}` | Partial update (`null` = unchanged) |
| `DELETE` | `/api/v1/babies/{id}` | Delete a baby |

### Pagination (`GET /api/v1/babies`)

| Parameter | Default | Description |
|-----------|---------|-------------|
| `page` | `0` | Page number (0-based) |
| `size` | `20` | Page size (max `100`; larger values are clamped by Spring) |
| `sort` | `createdAt,desc` | Sort field and direction |

Allowed sort fields: `createdAt`, `firstName`, `lastName`, `dateOfBirth`, `id`

Example:

```bash
curl "http://localhost:8080/api/v1/babies?page=0&size=10&sort=firstName,asc"
```

Response shape:

```json
{
  "content": [ ... ],
  "page": 0,
  "size": 10,
  "totalElements": 42,
  "totalPages": 5,
  "first": true,
  "last": false
}
```

### Example requests

```bash
# Create → 201
curl -s -X POST http://localhost:8080/api/v1/babies \
  -H 'Content-Type: application/json' \
  -d '{"firstName":"Ada","lastName":"Lovelace","dateOfBirth":"2024-06-15","sex":"FEMALE"}'

# List (paginated) → 200
curl -s "http://localhost:8080/api/v1/babies?page=0&size=20"

# Get one → 200
curl -s http://localhost:8080/api/v1/babies/1

# Partial update → 200
curl -s -X PATCH http://localhost:8080/api/v1/babies/1 \
  -H 'Content-Type: application/json' \
  -d '{"lastName":"Byron"}'

# Empty PATCH → 400
curl -s -X PATCH http://localhost:8080/api/v1/babies/1 \
  -H 'Content-Type: application/json' \
  -d '{}'

# Delete → 204
curl -s -o /dev/null -w "%{http_code}" -X DELETE http://localhost:8080/api/v1/babies/1

# Not found → 404
curl -s http://localhost:8080/api/v1/babies/999
```

### Validation

- `dateOfBirth` must not be in the future (`@PastOrPresent` on create and PATCH)
- `firstName` and `lastName` are required on create
- `sex` is optional (`MALE`, `FEMALE`, or `UNKNOWN`)

## Project phases

- **Phase 0:** Bootstrap — Spring Boot, JPA, Flyway, Actuator, PostgreSQL
- **Phase 1:** Baby CRUD
- **Phase 2:** Pagination and OpenAPI — current scope
- **Later:** Care records, authentication, baby-scoped RBAC

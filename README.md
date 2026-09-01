# Employee Management System

A plain Spring Boot REST API — no Docker, Jenkins, or CI/CD wiring included.
You'll containerize and orchestrate this yourself as a separate learning step;
the app just needs to run standalone with `mvn` and expose HTTP endpoints for
other services (or a pipeline) to call later.

## Tech stack

- Java 17
- Spring Boot 3.3 (Web, Data JPA, Validation, Actuator)
- H2 in-memory database by default (zero setup) — a `mysql` profile is included
  for when you point it at a real database
- Maven

## Project layout

```
src/main/java/com/devops/ems/
├── EmsApplication.java        # entry point
├── model/                     # JPA entities + enums
├── dto/                       # request/response payloads
├── repository/                # Spring Data JPA repositories
├── service/                   # business logic
├── controller/                # REST endpoints
├── exception/                 # custom exceptions + global handler
└── config/                    # sample data seeder (dev profile only)
src/main/resources/
├── application.properties         # shared config, profile switch
├── application-dev.properties     # H2 (default)
└── application-mysql.properties   # MySQL (opt-in)
```

## Running it

```bash
# from the project root
mvn spring-boot:run
```

The app starts on **http://localhost:8080** using the H2 in-memory database,
pre-loaded with a few sample employees so you have something to query
immediately.

- H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:emsdb`, user `sa`, no password)
- Health check: http://localhost:8080/actuator/health

To build a runnable jar instead:

```bash
mvn clean package
java -jar target/employee-management-system.jar
```

### Switching to MySQL later

1. Have a MySQL instance reachable (locally, in a container, wherever).
2. Run with the `mysql` profile:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=mysql
   ```
3. Connection settings come from environment variables (`DB_URL`,
   `DB_USERNAME`, `DB_PASSWORD`), with local defaults baked in — the same
   pattern you'll want when these get set as container env vars or
   Kubernetes secrets later.

## API reference

Base path: `/api/v1/employees`

| Method | Path | Description |
|---|---|---|
| POST | `/api/v1/employees` | Create an employee |
| GET | `/api/v1/employees` | List all employees |
| GET | `/api/v1/employees?department=DEVOPS` | Filter by department |
| GET | `/api/v1/employees?status=ACTIVE` | Filter by status |
| GET | `/api/v1/employees?search=priya` | Search by first/last name |
| GET | `/api/v1/employees/{id}` | Get one employee |
| GET | `/api/v1/employees/{id}/direct-reports` | List an employee's direct reports |
| PUT | `/api/v1/employees/{id}` | Full update |
| PATCH | `/api/v1/employees/{id}/status?status=ON_LEAVE` | Update status only |
| DELETE | `/api/v1/employees/{id}` | Delete an employee |
| GET | `/api/v1/employees/stats/headcount` | Headcount grouped by department |
| GET | `/api/v1/employees/stats/average-salary?department=ENGINEERING` | Avg salary in a department |

Departments: `ENGINEERING, DEVOPS, HUMAN_RESOURCES, FINANCE, SALES, MARKETING, SUPPORT`
Status values: `ACTIVE, ON_LEAVE, TERMINATED`

### Example requests

Create an employee:

```bash
curl -X POST http://localhost:8080/api/v1/employees \
  -H "Content-Type: application/json" \
  -d '{
        "firstName": "Ravi",
        "lastName": "Kumar",
        "email": "ravi.kumar@example.com",
        "phoneNumber": "9876543210",
        "department": "DEVOPS",
        "jobTitle": "Site Reliability Engineer",
        "salary": 92000,
        "hireDate": "2024-02-01",
        "managerId": 1
      }'
```

List employees in the DevOps department:

```bash
curl "http://localhost:8080/api/v1/employees?department=DEVOPS"
```

Update status:

```bash
curl -X PATCH "http://localhost:8080/api/v1/employees/2/status?status=ON_LEAVE"
```

Delete:

```bash
curl -X DELETE http://localhost:8080/api/v1/employees/2
```

### Error responses

Validation errors, not-found, and duplicate-email conflicts all return a
consistent JSON shape:

```json
{
  "timestamp": "2026-09-01T10:15:30",
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found with id: 99",
  "path": "/api/v1/employees/99"
}
```

## Running with Docker

```bash
docker compose up --build
```

This builds the app image (multi-stage: Maven build → slim JRE runtime),
starts a MySQL container, and wires the app to it via the `mysql` Spring
profile. First boot takes a bit longer while MySQL initializes and Maven
downloads dependencies — subsequent builds are cached.

- App: http://localhost:8080/api/v1/employees
- Health: http://localhost:8080/actuator/health
- MySQL: `localhost:3306`, db `emsdb`, user `root`, password `password`

Stop and remove containers:
```bash
docker compose down
```
Stop and also wipe the database volume:
```bash
docker compose down -v
```

See `docs/devops-roadmap.md` for the full path from here to a Jenkins +
Kubernetes + Terraform + LocalStack pipeline.

## What's intentionally left out

No `Dockerfile`, `docker-compose.yml`, `Jenkinsfile`, or any CI/CD config is
included — this is just the Java application, ready for you to wrap in your
own container and pipeline setup as a separate exercise. A couple of details
were added specifically to make that later step easier without adding any
pipeline logic now:

- **Spring Boot Actuator** exposes `/actuator/health`, a natural fit for a
  Docker `HEALTHCHECK` or Kubernetes liveness/readiness probe.
- **`application-mysql.properties`** reads its connection info from
  environment variables, matching how you'd inject config into a container
  or Kubernetes Secret/ConfigMap.

Neither of these does anything on its own — they're just there so the app
doesn't need code changes when you get to that stage.

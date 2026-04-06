# JAC Manager API

> RESTful API for managing Juntas de Acción Comunal (JAC) — Colombian community organizations.
> Provides comprehensive management of members, affiliations, treasury operations,
> and community governance processes through a reactive, secure, and scalable architecture.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Environment Configuration](#environment-configuration)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [Code Quality](#code-quality)
- [CI/CD Workflows](#cicd-workflows)
- [Versioning](#versioning)
- [Contributing](#contributing)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.0.5 |
| Reactive Stack | Spring WebFlux + Project Reactor |
| Database Access | Spring Data R2DBC |
| Database | PostgreSQL 17 |
| Security | Spring Security |
| Mapping | MapStruct 1.6.3 |
| Boilerplate | Lombok 1.18.44 |
| Build Tool | Maven 3.9.14 |
| Containerization | Docker + Docker Compose |

---

## Architecture

The project adopts a **Modular MVC architecture**. Each feature or domain is a self-contained
module with its own internal MVC layer structure. This keeps concerns isolated per domain
and makes each module independently navigable and maintainable.

### Project structure

```
src/
└── main/
    └── java/
        └── com/api/manager/jac/
            │
            ├── users/                        # Users module
            │   ├── controllers/
            │   │   └── UserController.java
            │   ├── services/
            │   │   ├── UserService.java
            │   │   └── UserServiceImpl.java
            │   ├── repositories/
            │   │   └── UserRepository.java
            │   ├── models/
            │   │   └── User.java
            │   ├── dtos/
            │   │   ├── UserRequestDto.java
            │   │   └── UserResponseDto.java
            │   ├── mappers/
            │   │   └── UserMapper.java
            │   ├── exceptions/
            │   │   └── UserNotFoundException.java
            │   ├── utils/
            │   │   └── UserUtils.java
            │   └── configs/
            │       └── UserConfig.java
            │
            ├── affiliations/                 # Affiliations module
            │   ├── controllers/
            │   ├── services/
            │   ├── repositories/
            │   ├── models/
            │   ├── dtos/
            │   ├── mappers/
            │   ├── exceptions/
            │   ├── utils/
            │   └── configs/
            │
            ├── treasury/                     # Treasury module
            │   ├── controllers/
            │   ├── services/
            │   ├── repositories/
            │   ├── models/
            │   ├── dtos/
            │   ├── mappers/
            │   ├── exceptions/
            │   ├── utils/
            │   └── configs/
            │
            └── shared/                       # Cross-cutting concerns
                ├── exceptions/
                │   └── GlobalExceptionHandler.java
                ├── utils/
                └── configs/
                    └── SecurityConfig.java
```

### Layer responsibilities

| Layer | Responsibility |
|---|---|
| `controllers` | Exposes HTTP endpoints, handles request/response |
| `services` | Business logic, orchestrates domain operations |
| `repositories` | R2DBC data access, database queries |
| `models` | Domain entities mapped to database tables |
| `dtos` | Request and response objects — never expose models directly |
| `mappers` | MapStruct interfaces to convert between models and DTOs |
| `exceptions` | Module-specific exceptions |
| `utils` | Stateless helpers specific to the module |
| `configs` | Module-level Spring beans and configuration |
| `shared` | Global exception handler, security config, and shared utilities |

### Rules

- A module must **never import** from another module's internal layers (models, repositories, etc.)
- If two modules need to share data, they do so through **DTOs or service interfaces** in `shared`
- All cross-cutting configuration (security, global exception handling) lives in `shared`
- New features always get their **own module directory** — no logic is added to existing modules unless it belongs to that domain

---

## Prerequisites

Before running the project locally, make sure you have installed:

- [Java 25](https://adoptium.net/) (Eclipse Temurin recommended)
- [Maven 3.9.14](https://maven.apache.org/download.cgi)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (required for Docker Compose and Testcontainers)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) (recommended IDE)

---

## Getting Started

```bash
# Clone the repository
git clone https://github.com/sebas679og/jac-manager-api.git
cd jac-manager-api

# Download dependencies
./mvnw dependency:resolve
```

---

## Docker Image

The application is available as a Docker image on Docker Hub. You can pull and run it by providing the required environment variables described below.

```bash
# Pull the latest image from Docker Hub
docker pull sebas679og/jac-manager-api:latest
```

```bash
# Run the container with the required environment variables
docker run -d \
  --name jac-manager-api \
  -p 8080:8080 \
  -e JAC_DATASOURCE_URL=jdbc:postgresql://host:5432/jac_db \
  -e JAC_DATASOURCE_USERNAME=your_username \
  -e JAC_DATASOURCE_PASSWORD=your_password \
  -e JAC_LEVEL_LOGIN=WARN \
  -e JAC_LEVEL_ROOT=INFO \
  sebas679og/jac-manager-api:latest
```

> 🐳 Full image documentation available on [Docker Hub](https://hub.docker.com/r/sebas679og/jac-manager-api).

---

## Environment Configuration

### 1. Create your local `.env` file

```bash
cp .env.template .env
```

The `.env.template` contains all required variables with default values for local development. 

> Never commit your `.env` file — it is already listed in `.gitignore`.

### 2. Start all services with Docker Compose

```bash
docker compose up -d
```

This starts:
- `postgres-jac` — PostgreSQL 17.8 on port `5432`
- `jac-service` — the API on port `8080`

```bash
# View logs
docker compose logs -f

# Stop all services
docker compose down

# Stop and remove volumes (wipes the database)
docker compose down -v
```

---

## Running the Application

### With Docker Compose (recommended)

```bash
docker compose up -d
```

The API will be available at `http://localhost:8080`.

### With Maven (without rebuilding the image)

If you want to run the app directly without Docker Compose, start only the database first:

```bash
# Start only the database
docker compose up -d postgres-jac

# Run the application
./mvnw spring-boot:run
```

---

## Testing

Tests use **Testcontainers** — Docker must be running before executing them.
There is no need to start any service manually; Testcontainers handles the database automatically.

```bash
# Run all tests
./mvnw clean test

# Run tests and generate coverage report
./mvnw -B clean verify "-Dspotless.check.skip=true" "-Dcheckstyle.skip=true" "-Dpmd.skip=true"

# Coverage report will be available at:
# target/site/jacoco/index.html
```

---

## Code Quality

The project enforces three quality gates. Run them before opening a PR:

```bash
# 1. Auto-fix code formatting (run this first)
./mvnw spotless:apply

# 2. Check formatting (what CI runs)
./mvnw spotless:check

# 3. Check code style
./mvnw checkstyle:check

# 4. Static analysis
./mvnw pmd:check

# Run all checks at once
./mvnw spotless:check checkstyle:check pmd:check
```

> **Tip:** Always run `spotless:apply` before committing to avoid formatting failures in CI.

---

## CI/CD Workflows

| Workflow | Trigger | Description |
|---|---|---|
| `Run Tests` | Pull Request to `dev` | Compiles and runs all tests with coverage |
| `Code Quality` | Pull Request to `dev` | Runs Spotless, Checkstyle and PMD |
| `Docker Build & Push` | Push to `main` | Builds and pushes image to Docker Hub |

---

## Versioning

This project follows [Semantic Versioning](https://semver.org/):

| Change type | Version bump | Example |
|---|---|---|
| Bug fix | Patch | `0.1.0 → 0.1.1` |
| New feature | Minor | `0.1.0 → 0.2.0` |
| Breaking change | Major | `0.1.0 → 1.0.0` |

To bump the version before merging to `main`:

```bash
./mvnw versions:set "-DnewVersion=0.2.0" "-DgenerateBackupPoms=false"
git add pom.xml
git commit -m "chore: bump version to 0.2.0"
```

---

## Contributing

See [CONTRIBUTING.md](./CONTRIBUTING.md) for the full contribution guide.

---

> Maintained by **Sebastian** · JAC Manager API © 2026
> 
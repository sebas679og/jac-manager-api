# JAC Manager API

> RESTful API for managing Juntas de Acción Comunal (JAC) — Colombian community organizations.
> Provides comprehensive management of members, affiliations, treasury operations,
> and community governance processes through a reactive, secure, and scalable architecture.

---

## Table of Contents

- [Tech Stack](#tech-stack)
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
./mvnw test

# Run tests and generate coverage report
./mvnw -B clean verify

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
./mvnw versions:set "-DnewVersion=0.2.0"
git add pom.xml
git commit -m "chore: bump version to 0.2.0"
```

---

## Contributing

See [CONTRIBUTING.md](./CONTRIBUTING.md) for the full contribution guide.

---

> Maintained by **Sebastian** · JAC Manager API © 2026

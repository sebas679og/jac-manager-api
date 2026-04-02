# Contributing to JAC Manager API

Thank you for contributing to JAC Manager API. This guide covers everything you need
to set up your environment, follow the project conventions, and get your changes merged.

---

## Table of Contents

- [Prerequisites](#prerequisites)
- [Setting Up the Local Environment](#setting-up-the-local-environment)
- [Architecture Convention](#architecture-convention)
- [Branch Strategy](#branch-strategy)
- [Commit Convention](#commit-convention)
- [Bumping the Version](#bumping-the-version)
- [Running the Application Locally](#running-the-application-locally)
- [Running Tests](#running-tests)
- [Code Quality Checks](#code-quality-checks)
- [Opening a Pull Request](#opening-a-pull-request)
- [CI/CD Gates](#cicd-gates)

---

## Prerequisites

Make sure you have the following installed before starting:

| Tool | Version | Download |
|---|---|---|
| Java (Eclipse Temurin) | 25 | https://adoptium.net |
| Maven | 3.9.14 | https://maven.apache.org/download.cgi |
| Docker Desktop | Latest | https://www.docker.com/products/docker-desktop |
| Git | Latest | https://git-scm.com |
| IntelliJ IDEA | Latest | https://www.jetbrains.com/idea (recommended) |

> **Docker is required** for both local development (Docker Compose) and running tests (Testcontainers).

---

## Setting Up the Local Environment

### 1. Clone the repository

```bash
git clone https://github.com/sebas679og/jac-manager-api.git
cd jac-manager-api
```

### 2. Download all dependencies

```bash
./mvnw dependency:resolve
```

### 3. Configure environment variables

```bash
cp .env.template .env
```

The `.env` file is pre-filled with default values for local development. You can modify these values if needed, but make sure to keep the same variable names.

> Never commit your `.env` file — it is already listed in `.gitignore`.

### 4. Start all services

```bash
docker compose up -d
```

This starts two services defined in `docker-compose.yml`:

| Service | Description | Port |
|---|---|---|
| `postgres-jac` | PostgreSQL 17.8 database | `5432` |
| `jac-service` | JAC Manager API | `8080` |

```bash
# View logs from all services
docker compose logs -f

# View logs from a specific service
docker compose logs -f jac-service

# Stop all services
docker compose down

# Stop and wipe the database volumes
docker compose down -v
```

### 5. Verify the setup

The API should be available at `http://localhost:8080`.

---

## Architecture Convention

The project adopts a **Modular MVC architecture**. Every new feature must follow this structure.

### Module structure

Each domain feature gets its own directory under `com/api/manager/jac/` with the
following internal layers:

```
com/api/manager/jac/
│
├── {module}/
│   ├── controllers/       # HTTP endpoints
│   ├── services/          # Business logic (interface + impl)
│   ├── repositories/      # R2DBC data access
│   ├── models/            # Database entities
│   ├── dtos/              # Request and response objects
│   ├── mappers/           # MapStruct interfaces
│   ├── exceptions/        # Module-specific exceptions
│   ├── utils/             # Stateless helpers
│   └── configs/           # Module-level Spring beans
│
└── shared/                # Cross-cutting concerns
    ├── exceptions/
    │   └── GlobalExceptionHandler.java
    ├── utils/
    └── configs/
        └── SecurityConfig.java
```

### Layer responsibilities

| Layer | Responsibility |
|---|---|
| `controllers` | Exposes HTTP endpoints, handles request/response mapping |
| `services` | Business logic — always define an interface and a separate `Impl` class |
| `repositories` | R2DBC data access and database queries |
| `models` | Domain entities mapped to database tables |
| `dtos` | Request and response objects — **never expose models directly to the API** |
| `mappers` | MapStruct interfaces to convert between models and DTOs |
| `exceptions` | Module-specific exceptions (e.g. `UserNotFoundException`) |
| `utils` | Stateless helper methods specific to the module |
| `configs` | Module-level Spring beans and configuration |
| `shared` | Global exception handler, security config, and utilities shared across modules |

### Adding a new module — step by step

**Example:** adding a `governance` module.

#### 1. Create the directory structure

```
com/api/manager/jac/governance/
├── controllers/
│   └── GovernanceController.java
├── services/
│   ├── GovernanceService.java
│   └── GovernanceServiceImpl.java
├── repositories/
│   └── GovernanceRepository.java
├── models/
│   └── Governance.java
├── dtos/
│   ├── GovernanceRequestDto.java
│   └── GovernanceResponseDto.java
├── mappers/
│   └── GovernanceMapper.java
├── exceptions/
│   └── GovernanceNotFoundException.java
├── utils/
│   └── GovernanceUtils.java
└── configs/
    └── GovernanceConfig.java
```

#### 2. Follow the naming convention

| Layer | Pattern | Example |
|---|---|---|
| Controller | `{Module}Controller` | `GovernanceController` |
| Service interface | `{Module}Service` | `GovernanceService` |
| Service implementation | `{Module}ServiceImpl` | `GovernanceServiceImpl` |
| Repository | `{Module}Repository` | `GovernanceRepository` |
| Model | `{Module}` | `Governance` |
| Request DTO | `{Module}RequestDto` | `GovernanceRequestDto` |
| Response DTO | `{Module}ResponseDto` | `GovernanceResponseDto` |
| Mapper | `{Module}Mapper` | `GovernanceMapper` |
| Exception | `{Module}{Reason}Exception` | `GovernanceNotFoundException` |

#### 3. Rules

- A module must **never import** from another module's internal layers (models, repositories, etc.)
- Inter-module communication goes through **DTOs or service interfaces** defined in `shared`
- All global concerns (security, global exception handling) live in `shared` — never in a module
- Do not add unrelated logic to an existing module — create a new one instead
- The `services` layer must always be defined as an **interface + implementation** pair

---

## Branch Strategy

We follow a two-level promotion flow:

```
feature/* ──┐
fix/*       ├──► dev ──► main
chore/*  ───┘
```

| Prefix | Use case |
|---|---|
| `feature/` | New functionality |
| `fix/` | Bug fixes |
| `chore/` | Maintenance, dependencies, config |
| `refactor/` | Code restructuring without behavior change |
| `docs/` | Documentation only |

**Rules:**
- Never commit directly to `dev` or `main`
- Always branch off from the latest `dev`
- One feature or fix per branch — one module per branch when possible
- PRs go to `dev` first — only `dev` merges into `main`

```bash
# Always start from dev
git checkout dev
git pull origin dev
git checkout -b feature/governance-module
```

---

## Commit Convention

We follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <short description>
```

| Type | Use case |
|---|---|
| `feat` | New feature |
| `fix` | Bug fix |
| `chore` | Build, dependencies, config |
| `refactor` | Refactoring |
| `test` | Adding or fixing tests |
| `docs` | Documentation |
| `style` | Formatting, no logic change |
| `perf` | Performance improvement |

**Examples:**

```bash
git commit -m "feat(treasury): add monthly balance calculation endpoint"
git commit -m "fix(affiliations): correct duplicate member validation"
git commit -m "chore: bump version to 0.2.0"
git commit -m "test(users): add integration test for user registration"
git commit -m "docs: update contributing guide with architecture section"
```

> Use the module name as the scope — `feat(treasury)`, `fix(users)`, `test(affiliations)`.

---

## Bumping the Version

Version follows [Semantic Versioning](https://semver.org/): `MAJOR.MINOR.PATCH`

| Change | Bump | Example |
|---|---|---|
| Bug fix | Patch | `0.1.0 → 0.1.1` |
| New feature | Minor | `0.1.0 → 0.2.0` |
| Breaking change | Major | `0.1.0 → 1.0.0` |

**When to bump:** Before opening your PR to `dev` if your change warrants a version change.

```bash
# Set new version in pom.xml
./mvnw versions:set "-DnewVersion=0.2.0" "-DgenerateBackupPoms=false"

# Commit the change
git add pom.xml
git commit -m "chore: bump version to 0.2.0"
```

> The Docker image tag is derived automatically from `pom.xml` when `dev` is merged into `main`.
> You do not need to create Git tags manually.

---

## Running the Application Locally

### With Docker Compose (recommended)

```bash
docker compose up -d
```

### With Maven only (faster for development iterations)

```bash
# Start only the database
docker compose up -d postgres-jac

# Run the application with Maven
./mvnw spring-boot:run
```

---

## Running Tests

> **Docker must be running** before executing tests. Testcontainers automatically
> spins up a PostgreSQL 17 container — no manual setup required.

```bash
# Run all tests
./mvnw test

# Run tests and generate JaCoCo coverage report
./mvnw -B clean verify

# Open coverage report
# target/site/jacoco/index.html
```

### Writing integration tests

All integration tests must extend `AbstractIntegrationTest`:

```java
class UserControllerTest extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldRegisterUser() {
        // your test
    }
}
```

`AbstractIntegrationTest` handles:
- Starting the PostgreSQL Testcontainer automatically
- Activating the `test` Spring profile
- Configuring `WebTestClient` for reactive endpoint testing

> Place integration tests mirroring the module structure:
> `src/test/java/com/api/manager/jac/users/controllers/UserControllerTest.java`

---

## Code Quality Checks

The project enforces three quality gates. **All must pass before opening a PR.**

### 1. Spotless — code formatting

```bash
# Auto-fix all formatting issues — run this before every commit
./mvnw spotless:apply

# Check only (what CI runs — fails if any file is not formatted)
./mvnw spotless:check
```

> Always run `spotless:apply` before `git commit` to avoid CI failures.

### 2. Checkstyle — code style

```bash
./mvnw checkstyle:check
```

Common issues and fixes:

| Issue | Fix |
|---|---|
| Line too long | Break into multiple lines (max 100 chars) |
| Wrong import order | Run `spotless:apply` — it fixes imports automatically |
| Missing Javadoc on public methods | Add `/** ... */` above the method |

### 3. PMD — static analysis

```bash
./mvnw pmd:check
```

If PMD flags a false positive on a valid pattern, suppress it on that specific element only:

```java
@SuppressWarnings("PMD.UseUtilityClass")
public class JacApplication {
    // ...
}
```

Never disable a PMD rule globally — suppress it only where it is a confirmed false positive.

### Run all checks at once

```bash
./mvnw spotless:check checkstyle:check pmd:check
```

### Recommended workflow before every commit

```bash
# 1. Fix formatting
./mvnw spotless:apply

# 2. Run all quality checks
./mvnw spotless:check checkstyle:check pmd:check

# 3. Run tests
./mvnw -B clean verify "-Dspotless.check.skip=true" "-Dcheckstyle.skip=true" "-Dpmd.skip=true"

# 4. If everything passes — commit and push
git add .
git commit -m "feat(module): your change description"
git push origin feature/your-branch
```

---

## Opening a Pull Request

1. Make sure all checks pass locally (see [recommended workflow](#recommended-workflow-before-every-commit))
2. Push your branch to origin
3. Open a PR from your branch **to `dev`** — never directly to `main`
4. Fill in the PR description with:
    - What module was added or modified
    - Type of change (`feat`, `fix`, `chore`, etc.)
    - Version bump applied (if any)
5. Wait for CI to pass — both workflows must be green:
    - ✅ Run Tests
    - ✅ Code Quality
6. Request a review

Once approved and merged to `dev`, a separate PR from `dev` to `main` triggers the
Docker build and push to Docker Hub.

**PRs will not be merged if CI is failing.**

---

## CI/CD Gates

| Workflow | Trigger | Checks                                          |
|---|---|-------------------------------------------------|
| `Run Tests` | Pull Request to `dev` | Compiles, runs all tests, uploads JaCoCo report |
| `Code Quality` | Pull Request to `dev` | Spotless, Checkstyle, PMD                       |
| `Docker Build & Push` | Push to `main` | Builds image, pushes to Docker Hub with version |

The Docker image is tagged with:
- `latest` — always points to the last merge to `main`
- `<version>` — from `pom.xml` (e.g. `0.2.0`)

---

> For questions or suggestions open an issue or contact **Sebastian**.
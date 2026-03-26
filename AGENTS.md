# Repository Guidelines for AI Coding Agents

## Project Structure & Module Organization
- `src/main/java`: Gateway, context registry, and SOAP/JSON translation logic.
- After `src/main/java/uk/gov/hmcts/darts/`, code is organized by domain area (e.g., `cases`, `audio`, `users`). Each domain folder may contain subfolders such as `controller`, `service`, `impl`, `exceptions`, etc., grouping related classes by responsibility within that domain.
- Shared/common code used across domains is placed in `src/main/java/uk/gov/hmcts/darts/common`.
- `src/main/resources/ws/`, `context/src/main/resources/ws/`: WSDLs and schemas (generated, never edited directly; see Gradle tasks).
- `src/functionalTest/`, `src/integrationTest/`, `src/smokeTest/`, `src/test/`, `src/testCommon/`: Test code, split by type.
- `bin/`: Helper scripts for secrets and Docker.
- `docker-compose-local.yml`, `docker-compose-local-to-staging.yml`: Compose files for local and hybrid dev.
- `build.gradle`: Build and dependency management.
- `charts/`, `config/`, `infrastructure/`: Operational and deployment assets.

## Build, Test, and Development Commands
- `./gradlew assemble`: Build the service and generate WSDLs/schemas.
- `./gradlew clean build`: Full clean build, including tests.
- `gradle clean processContextRegistryWSDL` / `gradle clean processDartsServiceWSDL`: Regenerate WSDLs/schemas (never edit by hand).
- `docker-compose -f docker-compose-local.yml build && docker-compose -f docker-compose-local.yml up`: Run all services locally.
- `docker-compose -f docker-compose-local-to-staging.yml build && docker-compose -f docker-compose-local-to-staging.yml up`: Run gateway+Redis locally, connect to remote infra.
- `curl http://localhost:8070/health`: Health check endpoint.
- `source bin/secrets-stg.sh`: Export secrets from Azure Key Vault (requires Azure CLI).
- See `src/functionalTest/README.md` for performance/functional test setup.

## Coding Style & Naming Conventions
- Java 21, 4-space indentation, 120-char lines, fail-fast warnings (Checkstyle/PMD enforced via Gradle).
- Checkstyle and PMD configs are in `config/checkstyle/` and `config/pmd/`.
- Use constructor injection and `@Slf4j` for logging.
- Packages: lowercase dot notation; classes/enums: PascalCase; beans: end with `Service`, `Controller`, or `Repository`.
- Never edit generated WSDL/schema files; always use Gradle tasks.
- Exception classes in `com.emc.documentum.fs.rt` use Documentum XML namespaces—update to DARTS-specific if Documentum is removed.
- All secrets/config from Azure Key Vault; never commit credentials.
- Use global properties in SoapUI/Postman, not project properties, to avoid leaking secrets.

## Testing Guidelines
- JUnit 5 for all test types; mirror source packages and name test classes `*Test`.
- Functional/performance tests: see `src/functionalTest/README.md`.
- Health check: always verify with `/health` endpoint after changes.
- Mock external dependencies (darts-api, Redis, etc.) for fast, deterministic tests.
- Cover translation, error handling, and integration boundaries.

## Code Review Guidance for Agents
- Use actionable, severity-tagged comments:
  - `[P0]: <Rule>`
  - `Problem: ...`
  - `Why: ...`
  - `Fix: ...`
- P0: Security flaws, data corruption, regressions—block PR.
- P1: Concurrency/resource leaks, inefficient cache/db usage, logging sensitive data—flag and require remediation.
- P2: Naming, duplication, doc gaps—note for future work.
- Prefer clarity, descriptive names, and established DARTS patterns for layering and error handling.
- Always check for existing helpers/utilities before adding new ones.

## Integration Points & External Dependencies
- **darts-api**: Main backend, expects JSON.
- **Redis**: Session/token cache.
- **Azure Key Vault**: All secrets; use provided scripts for local export.
- **External SOAP Clients**: WSDLs at `/service/darts/DARTSService?wsdl` and `/service/darts/runtime/ContextRegistryService?wsdl`.
- Sample SoapUI/Postman projects in root for manual testing.

## Security & Configuration Tips
- Never commit secrets; always source from Key Vault.
- Use provided scripts for secrets and Docker Compose for local parity.
- Redis is required for session/token cache; ensure it's running locally or via Compose.
- Prefer environment variables for config; see README for full list.
- **Note:** Keep all environment variable and secret documentation in sync with the list in `README.md`. Do not duplicate secrets here.

## Commit & Pull Request Guidelines
- Prefix messages with ticket or imperative (`DMP-123`, `fix:`, `refactor:`).
- Squash WIP commits; PRs need a short description, linked Jira, and test evidence.
- Confirm CI (Gradle, Docker, tests) is green before review.

## Key Files & Directories
- `README.md`: Full setup, environment, troubleshooting.
- `build.gradle`: Build config.
- `src/main/resources/ws/`, `context/src/main/resources/ws/`: WSDLs/schemas (generated only).
- `bin/secrets-stg.sh`: Export secrets from Key Vault.
- `docker-compose-local.yml`, `docker-compose-local-to-staging.yml`: Local/hybrid dev.
- `src/functionalTest/`: Functional/performance test code.
- Shared/common code: `src/main/java/uk/gov/hmcts/darts/common/`
- Example controller: `src/main/java/uk/gov/hmcts/darts/common/controllers/RootController.java`
- Example service: `src/main/java/uk/gov/hmcts/darts/cases/service/CaseService.java`
- Example implementation: `src/main/java/uk/gov/hmcts/darts/cases/impl/CaseInfoRouteImpl.java`
- Example test: `src/test/java/uk/gov/hmcts/darts/cases/impl/CaseInfoRouteImplTest.java`

## Adding New Features
When adding new features, create a new domain folder under `uk/gov/hmcts/darts/` if one does not exist. Place controllers, services, implementations, and exceptions in subfolders within the relevant domain area, following the established structure.

---

For more, see `README.md` and comments in key scripts. Always use provided scripts/tasks for build, secrets, and integration workflows.

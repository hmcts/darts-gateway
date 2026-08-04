You are an expert in Java 21, Spring Boot, SOAP services, WSDL-driven integration, and HMCTS service delivery. You write safe, maintainable code that preserves external contracts and keeps local developer workflows reliable.

## Repo Context
- DARTS Gateway translates SOAP requests from external services into JSON requests to DARTS API.
- The service is a Gradle-based Java 21 Spring Boot application.
- The repository includes the main gateway application plus a `context` module for the Documentum Context Registry SOAP API.
- Local runtime dependencies include DARTS API and Redis.
- Builds consume the DARTS API OpenAPI artifact from HMCTS Azure Artifacts or the local Maven repository.
- The repo includes WSDLs, XSDs, generated-source workflows, SoapUI projects, Postman collections, Docker Compose files, and Jenkins pipelines.

## Working Principles
- Preserve SOAP, WSDL, XSD, authentication, and downstream DARTS API contracts unless the task explicitly changes them.
- Treat generated code and generated WSDL outputs as contract artefacts; regenerate them through Gradle tasks rather than hand-editing generated files.
- Be cautious with token, Redis cache, basic-auth, and service-context handling because this gateway brokers access for multiple external systems.
- Never commit credentials, copied Key Vault values, tokens, PII, or local-only secret configuration.
- Use the Gradle wrapper (`./gradlew`) rather than a globally installed Gradle binary.

## Build And Local Run
- Build with `./gradlew build`.
- Use `./gradlew assemble` when preparing local runnable artifacts.
- Run all style checks with `./gradlew runAllStyleChecks`.
- The local health endpoint is `http://localhost:8070/health`.
- Preserve the documented Docker flows:
  - `docker-compose -f docker-compose-local build`
  - `docker-compose -f docker-compose-local up`
  - `docker-compose -f docker-compose-local-to-staging.yml build`
  - `docker-compose -f docker-compose-local-to-staging.yml up`
- IntelliJ remote debugging is documented on Docker debug port `8002`.

## Environment And Secrets
- Local execution requires README-documented environment variables, many backed by `darts-stg` Azure Key Vault secrets.
- Prefer the existing `source bin/secrets-stg.sh` workflow after Azure CLI login.
- Some local setups also require running the DARTS API secrets script from the `darts-api` repository.
- If you add, rename, or remove required environment variables, update `README.md` in the same change.

## Testing
- The build uses JUnit Platform.
- Test source sets include `test`, `testCommon`, `functionalTest`, `integrationTest`, and `smokeTest`.
- Keep shared test utilities in `src/testCommon/java`.
- Functional performance tests are selected from `src/functionalTest/java` by the `functionalPerformance` task and normal functional tests exclude `**/performance/*`.
- Integration tests may depend on Redis; Redis connection failures can indicate a stale local Redis instance.
- Preserve Gradle task names because CI and local workflows may depend on them.

## Generated Code And Contract Tasks
- OpenAPI model/interface generation depends on the `com.github.hmcts:darts-api:latest.release:openapi` artifact.
- Keep `extractOpenSpecification` and `generateCodeFromSpecification` working; do not hand-edit generated OpenAPI sources under `build/generated/openapi`.
- Legacy DARTS SOAP WSDL processing uses `./gradlew clean processDartsServiceWSDL`.
- Context Registry WSDL processing uses `./gradlew clean processContextRegistryWSDL`.
- Main-module JAXB generation includes schema tasks such as `genJaxb`, `genJaxbAddCase`, `genJaxbRegisterNode`, `genJaxbAddAudio`, `genJaxbViqEvent`, and `genJaxbCaseInfo`.
- The `context` module generates Context Registry client/server sources through `wsdl2java`.

## SOAP, MTOM, SoapUI, And Postman
- Keep `README-DARTSService-soapui-project.xml`, `README-ContextRegistryService-soapui-project.xml`, and `DARTS-Gateway.postman_collection.json` aligned with behaviour changes where practical.
- Do not add SoapUI credentials as project properties because they can be saved back into committed XML files.
- Preserve the documented local WSDL endpoints:
  - `http://localhost:8070/service/darts/DARTSService?wsdl`
  - `http://localhost:8070/service/darts/runtime/ContextRegistryService?wsdl`
- Treat add-audio and MTOM handling carefully; SoapUI is required for add-audio testing according to the README.

## Code Quality
- Keep code compatible with Java 21 and the configured Gradle toolchain.
- Avoid broad refactors around generated packages, excluded legacy packages, or contract models unless the task requires it.
- Prefer focused Spring components, explicit names, and clear mapping boundaries between SOAP payloads and DARTS API clients.
- Keep logging useful without exposing secrets, tokens, payloads containing sensitive data, or credentials.
- When adding dependencies, consider security exclusions and existing dependency constraints in `build.gradle`.

## Review Guidelines
- Prioritise issues that break SOAP compatibility, generated contract workflows, Docker startup, Redis integration, authentication, token caching, or DARTS API client generation.
- Treat undocumented changes to ports, environment variables, Key Vault secret names, WSDLs, XSDs, generated artefacts, or manual test assets as important.
- Check that new behaviour is covered in the right test source set and that generated assets are updated when contracts change.

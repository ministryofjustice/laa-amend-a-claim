# AGENTS.md

## Guidance

- Sister app: `../laa-submit-a-bulk-claim` - [repo](https://github.com/ministryofjustice/laa-submit-a-bulk-claim)
- Use it to align shared claim terminology and journey wording.

## Architecture

- App code: `src/main`
- Related services:
  - All available at `../`
  - `../laa-data-claims-api` - claims data store - [repo](https://github.com/ministryofjustice/laa-data-claims-api)
  - `../laa-submit-a-bulk-claim` - sister UI - [repo](https://github.com/ministryofjustice/laa-submit-a-bulk-claim)
  - `../laa-oidc-mock-server` - local OIDC - [repo](https://github.com/ministryofjustice/laa-oidc-mock-server)
- Other dependencies: Provider Details API, Redis
- Auth: SILAS (Azure Entra)

## Code standards

- Follow existing Spring Boot, MVC, Thymeleaf, and MapStruct patterns.
- Java uses **Google Java Format** via Spotless.
- Run:

```sh
./gradlew spotlessApply checkstyleAll
```

## Testing

Only run the required tests.

```sh
./gradlew test
./gradlew integrationTest
./gradlew pactTest
```


# laa-amend-a-claim
[![Ministry of Justice Repository Compliance Badge](https://github-community.service.justice.gov.uk/repository-standards/api/laa-spring-boot-microservice-template/badge)](https://github-community.service.justice.gov.uk/repository-standards/laa-spring-boot-microservice-template)

### ⚠️ WORK IN PROGRESS ⚠️
## Overview

The project uses the `laa-spring-boot-gradle-plugin` Gradle plugin which provides
sensible defaults for the following plugins:

- [Checkstyle](https://docs.gradle.org/current/userguide/checkstyle_plugin.html)
- [Dependency Management](https://plugins.gradle.org/plugin/io.spring.dependency-management)
- [Jacoco](https://docs.gradle.org/current/userguide/jacoco_plugin.html)
- [Java](https://docs.gradle.org/current/userguide/java_plugin.html)
- [Maven Publish](https://docs.gradle.org/current/userguide/publishing_maven.html)
- [Spring Boot](https://plugins.gradle.org/plugin/org.springframework.boot)
- [Test Logger](https://github.com/radarsh/gradle-test-logger-plugin)
- [Versions](https://github.com/ben-manes/gradle-versions-plugin)

The plugin is provided by -  [laa-spring-boot-common](https://github.com/ministryofjustice/laa-spring-boot-common), where you can find
more information regarding setup and usage.


#### Creating a GitHub Token

1. Ensure you have created a classic GitHub Personal Access Token with the following permissions:
   1. repo
   2. write:packages
   3. read:packages
2. The token **must be authorised with (MoJ) SSO**.
3. Add the following parameters to `~/.gradle/gradle.properties`

```
project.ext.gitPackageUser = <your GitHub username>
project.ext.gitPackageKey = <your GitHub access token>

```

#### Filling out .env

Using the `.env-template` file as a template, copy to a new .env file
`cp .env-template .env`

Be sure to fill out all values as they are required for pulling dependencies for the application to run

### Build And Run Application
1. Run `docker-compose up` to start wiremock server
2. Ensure that all environment variables from `.env` are set using:<br>
`export $(grep -v '^#' .env | xargs)`
3. Note: to run the application without Silas integration (Silas integration is a work in progress), use the local Spring profile with:<br>
`./gradlew bootRun --args='--spring.profiles.active=local'`
4. Navigate to the landing page at [http://localhost:8080/](http://localhost:8080/)

### Build application
`./gradlew clean build`

### Run integration tests
`./gradlew integrationTest`

### Run application via Docker
`docker compose up`

#### Swagger UI
- http://localhost:8080/swagger-ui/index.html

#### API docs (JSON)
- http://localhost:8080/v3/api-docs

### Actuator Endpoints
The following actuator endpoints have been configured:
- http://localhost:8080/actuator
- http://localhost:8080/actuator/health

#### 5. GitHub workflow
The following workflows have been provided:

* Build and test PR - `build-test-pr.yml`
* Build and deploy after PR merged - `pr-merge-main.yml` 

## Additional Information

### Libraries Used
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/reference/actuator/index.html) - used to provide various endpoints to help monitor the application, such as view application health and information.
- [Spring Boot Web](https://docs.spring.io/spring-boot/reference/web/index.html) - used to provide features for building the REST API implementation.
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/jpa.html) - used to simplify database access and interaction, by providing an abstraction over persistence technologies, to help reduce boilerplate code.
- [Springdoc OpenAPI](https://springdoc.org/) - used to generate OpenAPI documentation. It automatically generates Swagger UI, JSON documentation based on your Spring REST APIs.
- [Lombok](https://projectlombok.org/) - used to help to reduce boilerplate Java code by automatically generating common
  methods like getters, setters, constructors etc. at compile-time using annotations.
- [MapStruct](https://mapstruct.org/) - used for object mapping, specifically for converting between different Java object types, such as Data Transfer Objects (DTOs)
  and Entity objects. It generates mapping code at compile code.
- [H2](https://www.h2database.com/html/main.html) - used to provide an example database and should not be used in production.

# E2E tests

This module contains the standalone E2E UI automation suite for the LAA Amend a Claim service.  
It uses Playwright (Java), JUnit 5 and Allure. The E2E project is isolated under src/e2e.

## Prerequisites

* Java 17 or 21
* Gradle 8.2 or higher
* Node.js (required for Allure CLI)
* Allure CLI: npm install -g allure-commandline
* Playwright browsers (auto-installed on first run)
* Docker (required for running dependencies locally or in CI)

## Environment Config (`.env`)

Example .env contents:

* UI_BASE_URL=http://localhost:8080
* P_USERNAME=<your test@devl.justice.gov.uk email>
* P_PASSWORD=<your test@devl.justice.gov.uk password>
* MFA_SECRET=<see below instructions>
* HEADLESS=true
* BROWSER=chromium
* API_BASE=https://reqres.in
* DB_PASSWORD=<see 1Password>

## MFA Secret

1. Go to https://mysignins.microsoft.com/security-info
2. Add sign-in method
3. Microsoft Authenticator
4. Set up a different authentication app
5. Next
6. Can't scan the QR code?
7. Make a note of the secret key, and add it to `.env`
8. Use a different authenticator app (e.g. Authy) and finish the setup
9. Change default sign-in method to "App based authentication or hardware token - code"

## Writing a test

Each test class extends `BaseTest`, and must override the implementation of `inserts`. This defines the data that will be inserted into the database before each test inside that class.

Here, the order is important. For example, we cannot insert a claim into the database without a corresponding submission.

Therefore, when defining these inserts, the following order should be followed:
1. Bulk submission
2. Submission (requires a bulk submission ID)
3. Claim (requires a submission ID)
4. Claim summary fee (requires a claim ID)
5. Calculated fee detail (requires a claim ID and claim summary fee ID)

After each test has been executed, the `AfterEach` hook:
1. Deletes any assessments that were created
2. Iterates back through the inserts in reverse order to delete the seeded data from the database (note again that the order is important here).

The insert classes define a list of parameters that will be substituted into the wildcards (`?`) inside the [SQL files](/src/e2e/test/resources/fixtures/db/claims). Additional wildcards and parameters can be added as needed.

## Run all E2E tests

`./e2e.sh`

## Run a particular test class

`./e2e.sh <class name>` e.g. `./e2e.sh SearchTest`

## Run a particular test

`./e2e.sh <class name.test name>` e.g. `./e2e.sh SearchTest.canSearchForClaim`

## Debug mode

1. Visible browser:

   In `.env` set `HEADLESS=false`

2. Slow-motion debugging:

   In `DriverFactory` add `.setSlowMo(150)` to the launch options

## Allure

### Generate static HTML
```
./e2e.sh --allure-report
./e2e.sh SearchTest --allure-report
./e2e.sh SearchTest.canSearchForClaim --allure-report
```

### Serve report
```
./e2e.sh --allure-serve
./e2e.sh SearchTest --allure-serve
./e2e.sh SearchTest.canSearchForClaim --allure-serve
```

### View report
```
cd src/e2e/build/allure-report
allure open .
```
or
```
cd src/e2e/build/allure-report
npx http-server -p 8085
Open: http://localhost:8085
```

### GitHub
The GitHub Actions pipeline uploads the Allure report as an artifact:
1. Download the `allure-report.zip` from the CI run
2. Unzip it
3. `cd` into the folder
4. `allure open .`

## Troubleshooting

1. Allure report shows 0 tests:

    Must serve via allure open or http-server, not file://

2. Playwright browser won't open:

    Use `HEADLESS=false`

3. MFA failing:

    Ensure MFA_SECRET is correct

4. Dotenv not loading:

    Ensure .env exists in repo root or src/e2e

5. UI timing out:

    Increase waitFor timeouts


## Debugging tips
* Use `page.pause()` to open Playwright Inspector  
* Use `System.out.println()` for debug logging  
* Screenshots automatically attach to Allure via `@AfterEach` 
* Use slow-motion `setSlowMo()` to watch UI steps  
* Increase wait times for slower CI environments  


## Project structure

```
src/e2e/
 ├── main/java/uk/gov/justice/laa/amend/claim/
 │    ├── drivers/DriverFactory.java
 │    ├── pages/
 │    │     ├── LoginPage.java
 │    │     └── SearchPage.java
 │    └── utils/
 │          ├── EnvConfig.java
 │          └── ApiUtils.java
 │
 ├── test/java/uk/gov/justice/laa/amend/claim/
 │    ├── base/BaseTest.java
 │    └── tests/
 │          ├── SearchTest.java
 │          └── ApiTest.java
 │
 ├── main/resources/allure.properties
 └── test/resources/junit-platform.properties
```

## ALLURE + JUNIT GRADLE CONFIG (SUMMARY)
```
tasks.withType(Test).configureEach {
    useJUnitPlatform()
    outputs.upToDateWhen { false }
    systemProperty "junit.jupiter.extensions.autodetection.enabled", "true"
    systemProperty "junit.platform.listeners.autodetection.enabled", "true"
    systemProperty "allure.results.directory", "$buildDir/allure-results"

    testLogging {
        events "passed", "failed", "skipped"
        showStandardStreams = true
    }
}
```
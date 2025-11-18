This module contains the standalone E2E UI automation suite for the LAA Amend a Claim service.  
It uses Playwright (Java), JUnit 5 and Allure. The E2E project is isolated under src/e2e.

============================================================
PREREQUISITES
============================================================

Java 17 or 21
Gradle 8.2 or higher
Node.js (required for Allure CLI)
Allure CLI: npm install -g allure-commandline
Playwright browsers (auto-installed on first run)
Docker (required for running dependencies locally or in CI)

============================================================
ENVIRONMENT CONFIG (.env)
============================================================

Example .env contents:

UI_BASE_URL=http://localhost:8080
USERNAME=test@example.com
PASSWORD=SuperSecret123
MFA_SECRET=BASE32SECRET
HEADLESS=true
BROWSER=chromium
API_BASE=https://reqres.in

============================================================
RUN ALL E2E TESTS (LOCAL)
============================================================

cd src/e2e
./gradlew clean test

Allure results are written to:

src/e2e/build/allure-results/

============================================================
DEBUG MODE (HEADLESS/OFF + SLOW-MO)
============================================================

Visible browser:
./gradlew test -Pheadless=false

Or using environment variables:
HEADLESS=false BROWSER=firefox ./gradlew test

Slow-motion debugging:
LaunchOptions.setSlowMo(150);

============================================================
RUN A SPECIFIC TEST
============================================================

./gradlew test --tests uk.gov.justice.laa.amend.claim.tests.SearchTest

Or a single method:
./gradlew test --tests "uk.gov.justice.laa.amend.claim.tests.SearchTest.canSearchForClaim"

============================================================
VIEW ALLURE REPORT (LOCAL)
============================================================

Generate static HTML:
./gradlew allureReport

Serve report:
./gradlew allureServe

Local URL:
http://localhost:9090

IMPORTANT:
Do not open index.html via "file://".  
This will always show 0 tests because browsers block AJAX when using local files.

To view reports properly:
cd allure-report
allure open .

Or:
cd allure-report
npx http-server -p 8085
Open: http://localhost:8085

============================================================
VIEW ALLURE REPORT GENERATED IN CI
============================================================

The GitHub Actions pipeline uploads the Allure report as an artifact:

1. Download the "allure-report.zip" from the CI run
2. Unzip it
3. DO NOT double-click index.html
   (opening via file:// will show a blank report)
4. Serve it locally:

Option 1:
allure open .

Option 2:
npx http-server -p 8085
Open http://localhost:8085

If you follow these steps, the CI report will show the correct test results.

============================================================
USEFUL GRADLE COMMANDS
============================================================

./gradlew clean
./gradlew test -Pheadless=false
./gradlew allureReport
./gradlew allureServe

============================================================
TROUBLESHOOTING
============================================================

Allure report shows 0 tests:
- Must serve via allure open or http-server, not file://

Playwright browser won't open:
- Use HEADLESS=false

MFA failing:
- Ensure MFA_SECRET is correct Base32 key

Dotenv not loading:
- Ensure .env exists in repo root or src/e2e

UI timing out:
- Increase waitFor timeouts

============================================================
DEBUGGING TIPS
============================================================

Use page.pause() to open Playwright Inspector  
Use System.out.println() for debug logging  
Screenshots automatically attach to Allure via @AfterEach  
Use slow-motion (setSlowMo) to watch UI steps  
Increase wait times for slower CI environments  

============================================================
E2E PROJECT STRUCTURE
============================================================

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

============================================================
ALLURE + JUNIT GRADLE CONFIG (SUMMARY)
============================================================

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
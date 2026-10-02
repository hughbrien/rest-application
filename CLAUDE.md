# CLAUDE.md - AI Assistant Guide for rest-application

## Project Overview

Spring Boot REST API demo application (v0.9.2) designed for observability testing with OpenTelemetry, AppDynamics, and Dynatrace. The application exposes REST endpoints with intentional performance degradation and error scenarios for monitoring system validation.

## Tech Stack

- **Language:** Java 25
- **Framework:** Spring Boot 4.1.1
- **Build:** Maven 3.9.16 (use Maven Wrapper: `./mvnw`)
- **Test:** JUnit 5 (via spring-boot-starter-test)
- **Template Engine:** Freemarker
- **Containerization:** Docker (Amazon Corretto 25 base image)
- **Orchestration:** Kubernetes (3-replica deployment)

## Build & Run Commands

```bash
# Build
./mvnw clean install

# Package JAR only
./mvnw clean package

# Run tests
./mvnw test

# Run locally (port 8083)
java -jar ./target/rest-application-0.9.2.jar

# Docker
docker build . -t restapplication:latest
docker run -p 8083:8083 restapplication:latest
```

## Project Structure

```
src/main/java/com/hugenet/controller/
  RestApplication.java       # @SpringBootApplication entry point (enables scheduling)
  GreetingController.java    # Primary API controller - /greeting, /machine, /application, /check_message
  WelcomeController.java     # Secondary controller - /welcome, /applicationx
  BasicController.java       # Intentional error endpoints under /api/basic/
  MemoryController.java      # Memory simulation - /addmemoryusage, /reducememoryusage, /reportmemoryuse
  Greeting.java              # Record: (long id, String content, String result, long dollarValue)
  Welcome.java               # Record: (long id, String content)

src/main/resources/
  application.properties     # server.port=8083, logging config
  static/index.html          # Interactive API tester UI

src/test/java/com/hugenet/controller/
  RestApplicationTests.java  # Spring context load test (JUnit 5)

manifest/                    # Kubernetes deployment YAML
Kubernetes/                  # Helm values for AppDynamics operators/collectors
otel-agent/                  # OpenTelemetry collector config
http_requests/               # IntelliJ HTTP Client test files
```

## API Endpoints

| Method | Path | Controller | Behavior |
|--------|------|-----------|----------|
| GET | `/greeting?name=` | GreetingController | Calls remote services; slow first 5 min of each hour |
| GET | `/machine` | GreetingController | Returns hostname |
| GET | `/application` | GreetingController | 20% chance of 10-15s delay (max 10/hour) |
| GET | `/check_message` | GreetingController | 90% success / 10% throws exception |
| GET | `/welcome?name=` | WelcomeController | Welcome message with counter |
| GET | `/applicationx` | WelcomeController | 1-5s random delay |
| GET | `/api/basic/null-error` | BasicController | Intentional NullPointerException |
| GET | `/api/basic/divide-by-zero` | BasicController | Intentional ArithmeticException |
| GET | `/api/basic/bad-path/{id}` | BasicController | Incorrect annotation demo |
| GET | `/api/basic/unhandled` | BasicController | Unhandled RuntimeException |
| GET | `/addmemoryusage?sizeMB=&count=` | MemoryController | Allocates large byte[] chunks into a Map |
| GET | `/reducememoryusage?count=` | MemoryController | Removes oldest chunks from the Map |
| GET | `/reportmemoryuse` | MemoryController | Reports chunk inventory and JVM memory stats |
| GET | `/actuator` | Spring Boot | Health/metrics actuator endpoints |

## Key Architectural Patterns

- **Intentional chaos:** Endpoints include `Thread.sleep()` delays and random exceptions for monitoring/observability testing. These are by design, not bugs.
- **Atomic counters:** `AtomicLong` used for thread-safe request counting across controllers.
- **External HTTP calls:** `GreetingController` makes outbound HTTP calls to hughbrien.com and steelstratus.com for distributed tracing demos.
- **Records for DTOs:** `Greeting` and `Welcome` are Java records, not classes.
- **System property injection:** `REMOTE_TEST_HOST` and `REMOTE_DEFAULT_PORT` configure remote service targets at startup.

## Configuration

| File | Key Settings |
|------|-------------|
| `application.properties` | `server.port=8083`, logging to `./application-output.log` |
| `pom.xml` | Java 25, Spring Boot 4.1.1, Paketo buildpacks |
| `Dockerfile` | Corretto 25, `-Xmn256m -Xmx768m`, exposes port 8083 |
| `manifest/rest-application.yaml` | Namespace `restapplication`, 3 replicas, LoadBalancer on 8083 |

## Testing

- **Unit tests:** `./mvnw test` - runs JUnit 5 via Spring Boot test starter
- **Manual API tests:** `http_requests/*.http` files (IntelliJ HTTP Client format)
- **Note:** Tests live in `com.hugenet.controller`, matching the main sources, so `@SpringBootTest` discovers `RestApplication` automatically

## CI/CD

- **GitHub Actions** (`.github/workflows/main.yml`): Manual-trigger workflow that posts deployment event JSON to a configured endpoint. Uses `API_TOKEN` and `ENDPOINT_URL` secrets.

## Important Notes for AI Assistants

1. **Do not "fix" intentional errors** in `BasicController.java` - the null pointers, division by zero, and bad annotations are deliberate for monitoring demos.
2. **Do not remove `Thread.sleep()` calls** - synthetic delays in `GreetingController` and `WelcomeController` simulate real-world latency for observability testing.
3. **No linting/formatting tools configured** - no Checkstyle, SpotBugs, or PMD plugins. Follow existing code style (standard Spring Boot conventions).
4. **Version is in `pom.xml`** - currently `0.9.2` (not a `-SNAPSHOT`), so the built artifact is `target/rest-application-0.9.2.jar`. Update `pom.xml` and the `JAR_FILE` arg in `Dockerfile` together when bumping versions.
5. **The app is stateless** - no database, no persistent storage. All counters reset on restart.
6. **Mockito runs as an explicit `-javaagent`** - `maven-dependency-plugin:properties` resolves the jar path for surefire's `argLine`. Self-attaching is disallowed on current JDKs, so keep both plugin blocks in `pom.xml`.
7. **Freemarker has no `templates/` directory** - the UI is `static/index.html`, so `spring.freemarker.check-template-location=false` is set deliberately.

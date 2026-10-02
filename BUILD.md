### Build Rest Application for Kubernetes

The version lives in a single place: `<version>` in `./pom.xml` (currently `0.9.2`).
The built artifact name follows it, so `Dockerfile`'s `JAR_FILE` arg and
`manifest/rest-application.yaml`'s image tag must be updated to match when it changes.

```xml
	<groupId>com.hugenet</groupId>
	<artifactId>rest-application</artifactId>
	<version>0.9.2</version>
	<name>rest-application</name>
	<description>Demo project for Spring Boot</description>
```

#### Prerequisites

Java 25 (Spring Boot 4.1.1 requires 17 or newer). `JAVA_HOME` should point at the JDK
and `$JAVA_HOME/bin` should be on `$PATH`:

```bash
brew install openjdk@25

# Register it with macOS so /usr/libexec/java_home and IntelliJ can find it:
sudo ln -sfn /opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk \
  /Library/Java/JavaVirtualMachines/openjdk-25.jdk

# In ~/.zshrc:
export JAVA_HOME="$(/usr/libexec/java_home -v 25)"
export PATH="$JAVA_HOME/bin:$PATH"

java -version   # openjdk version "25..."
```

The Maven wrapper (`./mvnw`) downloads Maven 3.9.16 on first use; no local Maven needed.

#### Build

```bash
git clone https://github.com/hughbrien/rest-application
cd ./rest-application

./mvnw clean install

ls ./target
java -jar ./target/rest-application-0.9.2.jar
```

The app listens on port 8083 (`server.port` in `application.properties`).
Health check: `curl http://localhost:8083/actuator/health`

#### Docker

```bash
docker build . -t restapplication:0.9.2
docker run -p 8083:8083 restapplication:0.9.2

# Multi-arch push
docker buildx build --platform linux/amd64,linux/arm64 \
  --push -t hughbrien/restapplication:0.9.2 .
```

#### Kubernetes

```bash
kubectl apply -f manifest/rest-application.yaml
kubectl get pods -n restapplication
```

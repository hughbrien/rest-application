#
# Run the Build and Deployoment
#

FROM amazoncorretto:25

WORKDIR /app

# Build with `./mvnw clean package` first; the jar name must match pom.xml <version>.
ARG JAR_FILE=target/rest-application-0.9.2.jar
COPY ${JAR_FILE} /app/rest-application.jar

# Must match server.port in application.properties.
EXPOSE 8083

CMD [ "java", "-Xmn256m", "-Xmx768m", "-jar", "/app/rest-application.jar" ]

### Build Rest Application for Kubernetes 

Check version number in ./pom.xml file. 

```
	<groupId>com.appdynamics</groupId>
	<artifactId>rest-appdynamics</artifactId>
	<version>0.0.2-SNAPSHOT</version>
	<name>rest-appdynamics</name>
	<description>Demo project for Spring Boot</description>
```

mvn clean install

docker build . -t rest-appdynamics:0.0.2-SNAPSHOT

docker buildx build --platform linux/amd64,linux/arm64 --push -t hughbrien/rest-appdynamics:0.0.2-SNAPSHOT .


### Build Rest Application for Kubernetes 
- Need an easy way to persist the VERSION number in the build process
- Check version number in ./pom.xml file.
```
	<groupId>com.application</groupId>
	<artifactId>rest-application</artifactId>
	<version>0.0.2-SNAPSHOT</version>
	<name>rest-application</name>
	<description>Demo project for Spring Boot</description>
```
- mvnw clean install

Ensure you have a recent version of Java.  I think 11 or later should work.  I have tested up through version 21
git clone  https://github.com/hughbrien/rest-application
```
cd ./rest-application

./mvnw install

Updates the pom.xml to update the Version

ls ./target

try
java -jar ./target/rest-application-0.0.4-SNAPSHOT.jar

cp ./target/rest-application-0.0.4-SNAPSHOT.jar




```


- docker build . -t rest-application:0.0.2-SNAPSHOT

- docker buildx build --platform linux/amd64,linux/arm64 --push -t hughbrien/rest-application:0.0.2-SNAPSHOT .


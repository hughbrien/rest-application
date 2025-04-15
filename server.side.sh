

nohup java -Dserver.port=8886 -javaagent:./splunk-otel-javaagent.jar -jar ./target/rest-application-0.0.4-SNAPSHOT.jar > app-side.log 2>&1 &


nohup java -Dserver.port=8887 -javaagent:./splunk-otel-javaagent.jar -jar ./target/rest-application-0.0.4-SNAPSHOT.jar > server-side.log 2>&1 &
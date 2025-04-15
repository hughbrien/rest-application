#
# Run the Build and Deployoment
#

FROM amazoncorretto:21.0.1

WORKDIR /app

COPY ./target/rest-application-0.0.4-SNAPSHOT.jar /app/rest-application-0.0.4-SNAPSHOT.jar

EXPOSE 8000
#CMD ["/bin/sh"]
CMD [ "java", "-Xmn256m", "-Xmx768m", "-jar", "/app/rest-application-0.0.4-SNAPSHOT.jar" ]

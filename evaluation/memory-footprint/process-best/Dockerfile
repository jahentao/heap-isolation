FROM openjdk:11
COPY ./target/process-best-1.0-SNAPSHOT.jar /usr/app/
WORKDIR /usr/app
ENTRYPOINT ["java", "-jar", "process-best-1.0-SNAPSHOT.jar"]

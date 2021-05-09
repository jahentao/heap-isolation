FROM openjdk:11
COPY ./target/process-worst-1.0-SNAPSHOT-jar-with-dependencies.jar /usr/app/
WORKDIR /usr/app
ENTRYPOINT ["java", "-jar", "process-worst-1.0-SNAPSHOT-jar-with-dependencies.jar"]

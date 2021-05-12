FROM majidmakki/multi-tenant-jvm:latest
COPY ./target/process-worst-1.0-SNAPSHOT-jar-with-dependencies.jar /usr/app/
WORKDIR /usr/app
ENTRYPOINT ["./openjdk_internal/bin/java", "-jar", "process-worst-1.0-SNAPSHOT-jar-with-dependencies.jar"]

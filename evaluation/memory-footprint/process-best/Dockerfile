FROM majidmakki/multi-tenant-jvm:latest
COPY ./target/process-best-1.0-SNAPSHOT.jar /usr/app/
WORKDIR /usr/app
ENTRYPOINT ["./openjdk_internal/bin/java", "-jar", "process-best-1.0-SNAPSHOT.jar"]

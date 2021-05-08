FROM majidmakki/multi-tenant-jvm:latest
COPY ./target/thread-best-1.0-SNAPSHOT.jar /usr/app/
WORKDIR /usr/app
ENTRYPOINT ["java", "-jar", "thread-best-1.0-SNAPSHOT.jar"]

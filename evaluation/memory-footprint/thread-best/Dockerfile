FROM majidmakki/multi-tenant-jvm:latest
COPY ./target/thread-best-1.0-SNAPSHOT-jar-with-dependencies.jar /usr/app/
COPY ./src/main/resources/tenant_01.jar /usr/app/
COPY ./src/main/resources/tenant_02.jar /usr/app/
COPY ./src/main/resources/tenant_03.jar /usr/app/
COPY ./src/main/resources/tenant_04.jar /usr/app/
COPY ./src/main/resources/tenant_05.jar /usr/app/
WORKDIR /usr/app
ENTRYPOINT ["openjdk_internal/bin/java", "--patch-module", "java.base=framework-1.0-SNAPSHOT.jar", "--add-exports", "java.base/dnet.mt.hi.framework=ALL-UNNAMED", "-jar", "thread-best-1.0-SNAPSHOT-jar-with-dependencies.jar"]
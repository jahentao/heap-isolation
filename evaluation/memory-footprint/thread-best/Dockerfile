FROM majidmakki/multi-tenant-jvm:latest
COPY ./target/thread-best-1.0-SNAPSHOT.jar /usr/app/
COPY ../tenant_01/target/tenant_01-1.0-SNAPSHOT.jar /usr/app/
COPY ../tenant_02/target/tenant_02-1.0-SNAPSHOT.jar /usr/app/
COPY ../tenant_03/target/tenant_03-1.0-SNAPSHOT.jar /usr/app/
COPY ../tenant_04/target/tenant_04-1.0-SNAPSHOT.jar /usr/app/
COPY ../tenant_05/target/tenant_05-1.0-SNAPSHOT.jar /usr/app/
WORKDIR /usr/app
ENTRYPOINT ["openjdk_internal/bin/java", "--patch-module", "framework-1.0-SNAPSHOT.jar", "--add-exports", "java.base/dnet.mt.hi.framework=ALL-UNNAMED", "-jar", "thread-best-1.0-SNAPSHOT.jar"]
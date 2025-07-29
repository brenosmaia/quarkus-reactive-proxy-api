FROM openjdk:21-jdk-slim
WORKDIR /app

COPY target/quarkus-app/lib/ /app/lib/
COPY target/quarkus-app/app/ /app/app/
COPY target/quarkus-app/quarkus/ /app/quarkus/
COPY target/quarkus-app/quarkus-run.jar /app/

ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]

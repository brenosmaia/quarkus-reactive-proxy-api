FROM openjdk:21-jdk-slim
WORKDIR /app

COPY target/quarkus-app/lib/ /app/lib/
COPY target/quarkus-app/app/ /app/app/
COPY target/quarkus-app/quarkus/ /app/quarkus/
COPY target/quarkus-app/quarkus-run.jar /app/

ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]

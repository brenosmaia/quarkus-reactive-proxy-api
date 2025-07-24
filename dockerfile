FROM openjdk:21-jdk-slim
WORKDIR /app

# Copia os diretórios e o JAR necessário para rodar no modo fast-jar
COPY target/quarkus-app/lib/ /app/lib/
COPY target/quarkus-app/app/ /app/app/
COPY target/quarkus-app/quarkus/ /app/quarkus/
COPY target/quarkus-app/quarkus-run.jar /app/

ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]

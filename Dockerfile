# ---------- build stage ----------
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

# ---------- runtime stage ----------
FROM eclipse-temurin:21-jre-jammy
# run as non-root
RUN useradd -u 10001 spring
WORKDIR /app

# Copy the fat jar
COPY --from=build /workspace/target/*-SNAPSHOT.jar /app/app.jar
# If you produce releases, change the wildcard accordingly.

# Expose app port (optional for Compose/K8s)
EXPOSE 8080

# Container-aware JVM settings; tune if needed
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=25"

# Healthcheck (requires curl); comment out if you don't use Actuator
# RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
# HEALTHCHECK --interval=30s --timeout=3s --start-period=20s \
#   CMD curl -sf http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1

USER spring
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]

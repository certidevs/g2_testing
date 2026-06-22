# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp clean package -DskipTests


# ---------- Runtime stage ----------
FROM eclipse-temurin:25-jdk-alpine

WORKDIR /app

ENV JAVA_OPTS="" \
    UPLOAD_DIR="/app/uploads" \
    LOG_DIR="/app/logs"

RUN addgroup -S spring \
    && adduser -S spring -G spring \
    && mkdir -p /app/uploads \
    && mkdir -p /app/uploads/images \
    && mkdir -p /app/uploads/temp \
    && mkdir -p /app/logs \
    && chown -R spring:spring /app

COPY --from=build --chown=spring:spring /app/target/*.jar /app/app.jar

# Solo funciona si uploads/ existe en tu repo
COPY --chown=spring:spring uploads/ /app/uploads/

USER spring

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "\
mkdir -p \"$UPLOAD_DIR\" \"$UPLOAD_DIR/images\" \"$UPLOAD_DIR/temp\" \"$LOG_DIR\" && \
exec java $JAVA_OPTS -jar /app/app.jar --server.port=${PORT:-8080} \
"]
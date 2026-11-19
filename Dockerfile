# ========================================================
# Stage 1: Build CitMobi application archive
# ========================================================
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Cache Maven dependencies
COPY pom.xml ./
RUN mvn dependency:resolve -B

# Copy application source code and compile
COPY src ./src
RUN mvn clean package -DskipTests -B

# ========================================================
# Stage 2: Minimal Production Runtime
# ========================================================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Create unprivileged application user
RUN groupadd -r citmobi && useradd -r -g citmobi -d /app citmobi

# Copy packaged archive and entrypoint script
COPY --from=builder /app/target/CitMobi-0.0.1-SNAPSHOT.war ./app.war
COPY entrypoint.sh ./entrypoint.sh

# Normalize CRLF line endings (if committed from Windows) and set permissions
RUN sed -i 's/\r$//' ./entrypoint.sh && \
    chmod +x ./entrypoint.sh && \
    chown -R citmobi:citmobi /app

# Run as non-root user
USER citmobi

# Expose default HTTP ports (8080 default, 10000 Render standard)
EXPOSE 8080 10000

ENV PORT=8080 \
    SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["./entrypoint.sh"]

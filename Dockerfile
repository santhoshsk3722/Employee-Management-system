# ---------- Stage 1: Build ----------
# Uses a full Maven+JDK image just to compile and package the jar.
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build

# Copy pom.xml first and download dependencies separately.
# This layer only re-runs when pom.xml changes, so code edits
# don't force a full dependency re-download on every build.
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Now copy the actual source and build the jar.
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Stage 2: Runtime ----------
# Slim JRE-only image - no Maven, no JDK, no build tools.
# This is what actually ships and runs.
FROM eclipse-temurin:17-jre-alpine AS runtime

# Run as a non-root user (good practice, matters more once this
# goes into Kubernetes with restricted security contexts).
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY --from=build /build/target/employee-management-system.jar app.jar

RUN chown appuser:appgroup app.jar
USER appuser

EXPOSE 8080

# Actuator health endpoint - Kubernetes will use the equivalent
# of this as a liveness/readiness probe later.
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]

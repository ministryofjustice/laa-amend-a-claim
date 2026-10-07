# Specify java runtime base image
FROM amazoncorretto:25.0.4-alpine3.24

# Fix SNYK-ALPINE324-ZLIB-20541555 / CVE-2026-85091 (zlib 1.3.2-r1), not yet in the base image
RUN apk upgrade --no-cache zlib

# Define a volume to safely store temporary files across restarts
VOLUME /tmp
WORKDIR /app

# Copy the JAR from the build output to the container
COPY laa-amend-a-claim-ui/build/libs/laa-amend-a-claim-ui-1.0.0.jar /app/application.jar

# Create a group and non-root user
RUN addgroup -S appgroup && adduser -u 1001 -S appuser -G appgroup

# Set the default user
USER 1001

# Expose the port that the application will run on
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "application.jar"]
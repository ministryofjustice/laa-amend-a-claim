# Specify java runtime base image
FROM eclipse-temurin:21

# Set up working directory in the container
RUN mkdir -p /opt/laa-amend-a-claim/
WORKDIR /opt/laa-amend-a-claim/

# Copy the JAR file into the container
COPY laa-amend-a-claim/build/libs/laa-amend-a-claim-service-1.0.0-SNAPSHOT.jar app.jar

# Expose the port that the application will run on
EXPOSE 8080

# Run the JAR file
CMD java -jar app.jar
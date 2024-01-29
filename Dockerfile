# Start with a base image containing Java runtime
FROM openjdk:21-jdk-slim as build

# The application's jar file
ARG JAR_FILE=target/ChatGptPdfApplication-1.0-SNAPSHOT.jar

# Add the application's jar to the container
COPY ${JAR_FILE} app.jar

# Run the jar file 
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom -Djava.io.tmpdir=/tmp","-jar","/app.jar"]
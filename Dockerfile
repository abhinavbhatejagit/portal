# Use the official OpenJDK base image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the Gradle build files and settings
COPY build.gradle settings.gradle gradlew /app/

# Giving executable permissions for the Gradle wrapper
RUN chmod +x gradlew

# Copy the Gradle wrapper files
COPY gradle /app/gradle

# Download and install the required dependencies
RUN ./gradlew dependencies

# Copy the application source code
COPY src /app/src

# Build the application
RUN ./gradlew build

# Set the entry point for the application
ENTRYPOINT ["java", "-jar", "build/libs/portal-0.0.1-SNAPSHOT.jar"]

# Expose the port that the application will run on
EXPOSE 8081

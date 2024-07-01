FROM openjdk:21-jdk
COPY target/test-0.0.1-SNAPSHOT.jar test-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "/test-0.0.1-SNAPSHOT.jar"]
FROM openjdk:11
EXPOSE 8080
EXPOSE 7001
RUN mkdir -p /app/
ADD build/libs/*.jar /app/myapp.jar
ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]
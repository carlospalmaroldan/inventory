
# Package stage
#
FROM openjdk:11-jre-slim
COPY  target/inventory-1.0-SNAPSHOT.jar /usr/local/inventory-1.0-SNAPSHOT.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar","/usr/local/inventory-1.0-SNAPSHOT.jar"]

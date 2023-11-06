FROM amazoncorretto:11

WORKDIR /app

COPY target/shareit-server-0.0.1-SNAPSHOT.jar server.jar

CMD ["java", "-Duser.timezone=UTC", "-jar", "server.jar"]
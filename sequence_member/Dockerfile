FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/sequence_member-0.0.1-SNAPSHOT.jar app.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "app.jar"]
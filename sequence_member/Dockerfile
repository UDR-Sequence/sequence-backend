FROM openjdk:17-jdk-slim
WORKDIR /app
COPY sequence_member/build/libs/your-app.jar app.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "app.jar"]
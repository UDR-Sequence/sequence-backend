FROM openjdk:21-jdk-slim
ARG JAR_FILE=build/libs/sequence_member-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} ./sequence_member-0.0.1-SNAPSHOT.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "./sequence_member-0.0.1-SNAPSHOT.jar"]

FROM openjdk:17

ADD /target/my-task-tracker-0.0.1-SNAPSHOT.jar back.jar

ENTRYPOINT ["java", "-jar", "back.jar"]
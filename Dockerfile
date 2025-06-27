FROM openjdk:21-jdk

COPY build/libs/ms-user-0.0.1.jar web-app-0.1.jar

ENTRYPOINT ["java","-jar","web-app-0.1.jar"]
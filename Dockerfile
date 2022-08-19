FROM openjdk:11.0.5-jre-stretch
VOLUME /tmp
EXPOSE 29903
ARG APP_NAME=ms-leganda.jar
ARG JAR_FILE=target/*.jar
ADD ${JAR_FILE} APP_JAR

ENTRYPOINT ["java","-jar", "-Dspring.profiles.active=recette", "APP_JAR"]


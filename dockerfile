FROM openjdk: openjdk-22
ARG JAR_FILE=target/*.jar

COPY config/_application-prod.yaml /config/_application-prod.yaml
COPY config/application.properties /config/application.properties

COPY ./resources /resources
COPY ./src/main/resources /src/main/resources

COPY target /
COPY ${JAR_FILE} jira-1.0.jar

ENTRYPOINT ["java","-jar","/jira-1.0.jar"]
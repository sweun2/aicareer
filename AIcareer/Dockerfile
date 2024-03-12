FROM openjdk:17
ARG JAR_FILE=build/libs/AIcareer-ROOT-plain.war
COPY ${JAR_FILE} app.war

ENTRYPOINT ["java","-jar","/app.war"]

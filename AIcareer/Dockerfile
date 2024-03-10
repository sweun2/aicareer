FROM openjdk:17
ARG EXPLODED_DIR=build/libs/exploded
COPY ${EXPLODED_DIR} /app
WORKDIR /app
ENTRYPOINT ["java", "-jar", "AIcareer-ROOT.jar"]

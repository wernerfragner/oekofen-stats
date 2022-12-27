# builds a docker image for arm v7 using a simple fat JAR (without layers)
FROM arm32v7/eclipse-temurin:17.0.4_8-jre
EXPOSE 8080
ARG JAR_FILE=./oekofen-collector-app-0.2.3.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
FROM maven:3.6.3-jdk-11-openj9

COPY src /usr/src/dulcinea/src
COPY pom.xml /usr/src/dulcinea/pom.xml
COPY start.sh /usr/src/dulcinea/start.sh

WORKDIR /usr/src/dulcinea

RUN mvn clean install
ENTRYPOINT ["./start.sh"]

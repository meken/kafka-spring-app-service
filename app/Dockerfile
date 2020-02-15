FROM azul/zulu-openjdk-alpine:8
VOLUME /tmp
COPY target/app.jar /app.jar
COPY AI-Agent.xml /resources/AI-Agent.xml
ADD https://github.com/microsoft/ApplicationInsights-Java/releases/download/2.5.1/applicationinsights-agent-2.5.1.jar  /resources/applicationinsights-agent-2.5.1.jar
ENTRYPOINT ["java", "-javaagent:/resources/applicationinsights-agent-2.5.1.jar", "-jar", "/app.jar"]
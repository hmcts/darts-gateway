# ARG must be before all "FROM"s
# renovate: datasource=github-releases depName=microsoft/ApplicationInsights-Java
ARG APP_INSIGHTS_AGENT_VERSION=3.7.5

 # renovate: datasource=github-releases depName=microsoft/ApplicationInsights-Java
FROM hmctspublic.azurecr.io/base/java:21-distroless

COPY lib/applicationinsights.json /opt/app/
COPY build/libs/darts-gateway.jar /opt/app/

EXPOSE 8070
ENTRYPOINT ["java","-Duser.timezone=UTC","-jar","/opt/app/darts-gateway.jar"]

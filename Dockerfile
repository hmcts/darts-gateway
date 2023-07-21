 # renovate: datasource=github-releases depName=microsoft/ApplicationInsights-Java
ARG APP_INSIGHTS_AGENT_VERSION=3.4.15
FROM hmctspublic.azurecr.io/base/java:17-distroless

COPY build/libs/darts-gateway.jar /opt/app/

EXPOSE 8070
CMD [ "darts-gateway.jar" ]

FROM hmctspublic.azurecr.io/base/java:17-distroless

COPY build/libs/darts-gateway.jar /opt/app/
EXPOSE 8070
ENTRYPOINT ["java","-Duser.timezone=UTC","-jar","/opt/app/darts-gateway.jar"]

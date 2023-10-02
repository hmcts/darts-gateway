# darts-gateway

[![Build Status](https://travis-ci.org/hmcts/darts-gateway.svg?branch=master)](https://travis-ci.org/hmcts/darts-gateway)

This project contains the APIM interfaces for:-

1) DAR Notification Restful Endpoint
2) DAR Notification SOAP Endpoint
3) DARTS Legacy SOAP Endpoint
4) Documentum Context Registry SOAP Endpoint

The jenkins pipeline installs these interfaces into APIM (via terraform) as well as the associated apim policies

## API/Terraform/APIM Policies

| API Name | API                                                         | Terraform                                            | Policy                                                           |
|----------|-------------------------------------------------------------|------------------------------------------------------|------------------------------------------------------------------|
| Documentum Context Registry SOAP Endpoint | [here](./src/main/resources/ws/ContextRegistryService.wsdl) | [here](./infrastructure/context-regisry-apim.tf)     | [here](./infrastructure/apim-policy/context-registry-policy.xml) |
| DARTS Legacy SOAP Endpoint | [here](./src/main/resources/ws/dartsService.wsdl)           | [here](./infrastructure/darts-service-api-apim.tf)        | [here](./infrastructure/apim-policy/darts-service-api-policy.xml)            |
| DAR Notification REST Endpoint | [here](./src/main/resources/openapi/gatewayNotify.yaml)     | [here](./infrastructure/dar-notification-openapi-apim.tf) | [here](./infrastructure/apim-policy/dar-notification-openapi-policy.xml)     |
| DAR Notification SOAP Endpoint | TBC                                                         | TBC                                                  | TBC                                                              |

## Configuring the WSDL APIs

### Updating the DARTS Legacy SOAP API

If there are updated to the legacy darts API please add all supporting files into the directory [here](/src/main/ws/dartsService)

### Updating the DAR Notification SOAP API

//TODO: Need to find the wsdl file for this

### Updating the Documentum Context Registry Restful API

If there are updated to the documentum context registry please ad all supporting files into the directory [here](/src/main/ws/contextRegistry)

## Building the new configuration

This project can be build by the gradle command :-

gradle clean build

Post build will find that the wsdl files directly under [here](/src/main/ws) will have updated and are ready for git commit. If you
are happy i.e. you have sanity tested by importing into APIM, then commit them so that terraform can install them into the relevant environments

### Building the new darts context registry wsdl

gradle clean processContextRegistryWSDL

### Building the new legacy darts wsdl

gradle clean processDartsServiceWSDL

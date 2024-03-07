#!/bin/bash

# To set the secrets in your shell, source this file ie. source ./bin/secrets-stg.sh

echo "Exporting secrets from Azure keyvault (darts-stg), please ensure you have \"az\" installed and you have logged in, using \"az login\"."

export AAD_B2C_TENANT_ID_KEY="$(az keyvault secret show --vault-name darts-stg --name AzureAdB2CTenantId | jq .value -r)"
export REDIS_CONNECTION_STRING="$(az keyvault secret show --vault-name darts-stg --name redis-connection-string  | jq .value -r)"
export MAX_FILE_UPLOAD_SIZE_MEGABYTES="$(az keyvault secret show --vault-name darts-stg --name MaxFileUploadSizeInMegabytes | jq .value -r)"
export AAD_B2C_ROPC_CLIENT_ID_KEY="$(az keyvault secret show --vault-name darts-stg --name AzureAdB2CFuncTestROPCClientId | jq .value -r)"
export AAD_B2C_CLIENT_ID_KEY="$(az keyvault secret show --vault-name darts-stg --name AzureAdB2CClientId | jq .value -r)"
export DAR_NOTIFY_EVENT_SECUREMENT_PASSWORD="$(az keyvault secret show --vault-name darts-stg --name darts-gateway-DarNotifyEventSecurementPassword | jq .value -r)"
export DAR_NOTIFY_EVENT_SECUREMENT_USERNAME="$(az keyvault secret show --vault-name darts-stg --name darts-gateway-DarNotifyEventSecurementUsername   | jq .value -r)"
export VIQ_EXTERNAL_USER_NAME="$(az keyvault secret show --vault-name darts-stg --name ViQExternalUserName | jq .value -r)"
export VIQ_EXTERNAL_PASSWORD="$(az keyvault secret show --vault-name darts-stg --name ViQExternalPassword | jq .value -r)"
export VIQ_INTERNAL_PASSWORD="$(az keyvault secret show --vault-name darts-stg --name ViQInternalPassword | jq .value -r)"
export CP_EXTERNAL_USER_NAME="$(az keyvault secret show --vault-name darts-stg --name CPExternalUserName | jq .value -r)"
export CP_EXTERNAL_PASSWORD="$(az keyvault secret show --vault-name darts-stg --name CPExternalPassword | jq .value -r)"
export CP_INTERNAL_PASSWORD="$(az keyvault secret show --vault-name darts-stg --name CPInternalPassword | jq .value -r)"
export XHIBIT_EXTERNAL_USER_NAME="$(az keyvault secret show --vault-name darts-stg --name XhibitExternalUserName | jq .value -r)"
export XHIBIT_EXTERNAL_PASSWORD="$(az keyvault secret show --vault-name darts-stg --name XhibitExternalPassword | jq .value -r)"
export XHIBIT_INTERNAL_PASSWORD="$(az keyvault secret show --vault-name darts-stg --name XhibitInternalPassword | jq .value -r)"
export EXTERNAL_SERVICE_BASIC_AUTHORISATION_WHITELIST="$(az keyvault secret show --vault-name darts-stg --name ExternalServiceBasicAuthorisationWhitelist | jq .value -r)"

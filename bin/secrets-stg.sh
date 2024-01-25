#!/bin/bash

# To set the secrets in your shell, source this file ie. source ./bin/secrets-stg.sh

echo "Exporting secrets from Azure keyvault (darts-stg), please ensure you have \"az\" installed and you have logged in, using \"az login\"."

export AAD_B2C_TENANT_ID_KEY="$(az keyvault secret show --vault-name darts-stg --name AzureAdB2CTenantId | jq .value -r)"
export REDIS_CONNECTION_STRING="$(az keyvault secret show --vault-name darts-stg --name redis-connection-string  | jq .value -r)"
export MAX_FILE_UPLOAD_SIZE_MEGABYTES="$(az keyvault secret show --vault-name darts-stg --name MaxFileUploadSizeInMegabytes | jq .value -r)"
export AAD_B2C_ROPC_CLIENT_ID_KEY="$(az keyvault secret show --vault-name darts-stg --name AzureAdB2CFuncTestROPCClientId | jq .value -r)"
export DAR_NOTIFY_EVENT_SECUREMENT_PASSWORD="$(az keyvault secret show --vault-name darts-stg --name darts-gateway-DarNotifyEventSecurementPassword | jq .value -r)"
export DAR_NOTIFY_EVENT_SECUREMENT_USERNAME="$(az keyvault secret show --vault-name darts-stg --name darts-gateway-DarNotifyEventSecurementUsername   | jq .value -r)"

#!/bin/bash

# To set the secrets in your shell, source this file ie. source ./bin/secrets-stg.sh

echo "Exporting secrets from Azure keyvault (darts-stg), please ensure you have \"az\" installed and you have logged in, using \"az login\"."

echo "AAD_B2C_TENANT_ID=$(az keyvault secret show --vault-name darts-stg --name AzureAdB2CTenantId | jq .value -r)"
echo "REDIS_CONNECTION_STRING=$(az keyvault secret show --vault-name darts-stg --name redis-connection-string | jq .value -r)"
echo "MAX_FILE_UPLOAD_SIZE_MEGABYTES=$(az keyvault secret show --vault-name darts-stg --name MaxFileUploadSizeInMegabytes | jq .value -r)"
echo "AAD_B2C_ROPC_CLIENT_ID=$(az keyvault secret show --vault-name darts-stg --name AzureAdB2CFuncTestROPCClientId | jq .value -r)"
echo "AAD_B2C_CLIENT_ID=$(az keyvault secret show --vault-name darts-stg --name AzureAdB2CClientId | jq .value -r)"
echo "DAR_NOTIFY_EVENT_SECUREMENT_PASSWORD=$(az keyvault secret show --vault-name darts-stg --name darts-gateway-DarNotifyEventSecurementPassword | jq .value -r)"
echo "DAR_NOTIFY_EVENT_SECUREMENT_USERNAME=$(az keyvault secret show --vault-name darts-stg --name darts-gateway-DarNotifyEventSecurementUsername | jq .value -r)"
echo "EXTERNAL_SERVICE_BASIC_AUTHORISATION_WHITELIST=$(az keyvault secret show --vault-name darts-stg --name ExternalServiceBasicAuthorisationWhitelist | jq .value -r)"
echo "VIQ_EXTERNAL_USER_NAME=$(az keyvault secret show --vault-name darts-stg --name ViQExternalUserName | jq .value -r)"
echo "VIQ_EXTERNAL_PASSWORD=$(az keyvault secret show --vault-name darts-stg --name ViQExternalPassword | jq .value -r)"
echo "VIQ_INTERNAL_PASSWORD=$(az keyvault secret show --vault-name darts-stg --name ViQInternalPassword | jq .value -r)"

echo "AZURE_STORAGE_CONNECTION_STRING=$(az keyvault secret show --vault-name darts-stg --name AzureStorageConnectionString | jq .value -r)"

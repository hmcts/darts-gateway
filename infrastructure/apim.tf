locals {
  key_vault_name           = "${var.product}-${var.env}"
  key_vault_resource_group = "${var.product}-service-${var.env}"
  api_mgmt_name            = "${var.env}" == "aat" ? "cft-api-mgmt-stg" : "cft-api-mgmt-${var.env}"
  api_mgmt_resource_group  = "cft-${var.env}-network-rg"
  api_mgmt_product_name    = "${var.product}-${var.component}"
  api_mgmt_api_name        = "${var.product}-${var.component}-api"
  api_base_path            = var.product
  url_darts_gateway        = "http://darts-gateway-${var.env}.service.core-compute-${var.env}.internal"
  url_swagger              = "https://raw.githubusercontent.com/hmcts/darts-gateway/demo/src/main/resources/test.wsdl"

  key_vault_subscription_id = {
    stg = {
      subscription = "74dacd4f-a248-45bb-a2f0-af700dc4cf68"
    }
    dev = {
      subscription = "867a878b-cb68-4de5-9741-361ac9e178b6"
    }
    demo = {
      subscription = "c68a4bed-4c3d-4956-af51-4ae164c1957c"
    }
  }
}

provider "azurerm" {
  subscription_id            = local.key_vault_subscription_id["${var.env}"].subscription
  skip_provider_registration = "true"
  features {}
  alias = "key_vault_darts"
}

# Include CNP module for setting up an APIM product
module "api_mgmt_product" {
  source                = "git@github.com:hmcts/cnp-module-api-mgmt-product?ref=master"
  name                  = local.api_mgmt_product_name
  approval_required     = "false"
  subscription_required = "false"
  api_mgmt_name         = local.api_mgmt_name
  api_mgmt_rg           = local.api_mgmt_resource_group
}

# Include CNP module for setting up an API on an APIM product
# Uses output variable from api_mgmt_product to set product_id
# content_format needs to be set to wsdl-link as specs are in WSDL format
module "api_mgmt_api" {
  source         = "git@github.com:hmcts/cnp-module-api-mgmt-api?ref=master"
  name           = local.api_mgmt_api_name
  display_name   = "Darts Gateway API"
  api_mgmt_name  = local.api_mgmt_name
  api_mgmt_rg    = local.api_mgmt_resource_group
  product_id     = module.api_mgmt_product.product_id
  path           = local.api_base_path
  service_url    = local.url_darts_gateway
  protocols      = ["http", "https"]
  api_type       = "soap"
  swagger_url    = local.url_swagger
  content_format = "wsdl-link"
  revision       = "1"
}

# Reference to file containing API policy
data "local_file" "api_policy" {
  filename = "${path.module}/apim-policy/api-policy.xml"
}

# Include CNP module for setting up a policy on an API
# Uses output variable from api_mgmt_api to set api_name
module "api-mgmt-api-policy" {
  source                 = "git@github.com:hmcts/cnp-module-api-mgmt-api-policy?ref=master"
  api_mgmt_name          = local.api_mgmt_name
  api_mgmt_rg            = local.api_mgmt_resource_group
  api_name               = module.api_mgmt_api.name
  api_policy_xml_content = data.local_file.api_policy.content
}

data "azurerm_api_management" "cft-api-mgmt" {
  name                = local.api_mgmt_name
  resource_group_name = local.api_mgmt_resource_group
}

data "azurerm_key_vault" "key_vault" {
  name                = local.key_vault_name
  resource_group_name = local.key_vault_resource_group
  provider            = azurerm.key_vault_darts
}

data "azuread_client_config" "current" {}

# Set key vault permissions
resource "azurerm_key_vault_access_policy" "key_vault_permissions" {
  key_vault_id = data.azurerm_key_vault.key_vault.id

  tenant_id = data.azuread_client_config.current.tenant_id
  object_id = data.azuread_client_config.current.object_id

  secret_permissions = [
    "Get"
  ]

  certificate_permissions = [
    "Get",
    "List"
  ]
}

# Assign get certificate permissions to APIM so APIM can access it
resource "azurerm_key_vault_access_policy" "kv_apim_policy" {
  key_vault_id = data.azurerm_key_vault.key_vault.id

  tenant_id = data.azuread_client_config.current.tenant_id
  object_id = data.azurerm_api_management.cft-api-mgmt.identity.0.principal_id

  secret_permissions = [
    "Get"
  ]

  certificate_permissions = [
    "Get",
    "List"
  ]
}

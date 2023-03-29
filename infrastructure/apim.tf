locals {
  key_vault_name = "${var.product}-${var.env}"
  key_vault_resource_group = "${var.product}-service-${var.env}"
  api_mgmt_name = "${var.env}" == "aat" ? "cft-api-mgmt-stg" : "cft-api-mgmt-${var.env}"
  api_mgmt_resource_group = "cft-${var.env}-network-rg"
  api_mgmt_product_name = "${var.product}-${var.component}"
  api_mgmt_api_name = "${var.product}-${var.component}-api"
  api_base_path = "${var.product}"
  url_darts_gateway = "http://darts-gateway-${var.env}.service.core-compute-${var.env}.internal"
  url_swagger = "https://raw.githubusercontent.com/hmcts/darts-gateway/demo/src/main/resources/test.wsdl"

  key_vault_subscription_id = {
    sbox = {
      subscription = "bf308a5c-0624-4334-8ff8-8dca9fd43783"
    }
    perftest = {
      subscription = "7a4e3bd5-ae3a-4d0c-b441-2188fee3ff1c"
    }
    aat = {
      subscription = "1c4f0704-a29e-403d-b719-b90c34ef14c9"
    }
    ithc = {
      subscription = "7a4e3bd5-ae3a-4d0c-b441-2188fee3ff1c"
    }
    ptlsbox = {
      subscription = "1497c3d7-ab6d-4bb7-8a10-b51d03189ee3"
    }
    preview = {
      subscription = "1c4f0704-a29e-403d-b719-b90c34ef14c9"
    }
    ptl = {
      subscription = "1baf5470-1c3e-40d3-a6f7-74bfbce4b348"
    }
    prod = {
      subscription = "8999dec3-0104-4a27-94ee-6588559729d1"
    }
    demo = {
      subscription = "1c4f0704-a29e-403d-b719-b90c34ef14c9"
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

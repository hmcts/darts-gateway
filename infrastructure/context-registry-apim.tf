locals {
  ctx-api_mgmt_name           = "sds-api-mgmt-${var.env}"
  ctx-api_mgmt_resource_group = "ss-${var.env}-network-rg"
  ctx-api_mgmt_product_name   = "${var.product}-${var.component}"
  ctx-api_mgmt_api_name       = "${var.product}-${var.component}-api-ctxt"
  ctx-api_base_path           = var.product
  ctx-url_darts_api_hostname  = "https://${var.api_hostname}"
  ctx-url_swagger             = "https://raw.githubusercontent.com/hmcts/darts-gateway/master/src/main/resources/ws/ContextRegistryService.wsdl"
}

provider "azurerm" {
  alias           = "ctx-aks-sdsapps"
  subscription_id = var.aks_subscription_id
  features {}
}

# Include CNP module for setting up an APIM product
module "ctx_api_mgmt_product" {
  source                = "git@github.com:hmcts/cnp-module-api-mgmt-product?ref=master"
  name                  = local.ctx-api_mgmt_product_name
  approval_required     = "false"
  subscription_required = "false"
  api_mgmt_name         = local.ctx-api_mgmt_name
  api_mgmt_rg           = local.ctx-api_mgmt_resource_group
  providers = {
    azurerm = azurerm.ctx-aks-sdsapps
  }
}

# Include CNP module for setting up an API on an APIM product
# Uses output variable from api_mgmt_product to set product_id
# content_format needs to be set to wsdl-link as specs are in WSDL format
module "ctx_api_mgmt_api" {
  source         = "git@github.com:hmcts/cnp-module-api-mgmt-api?ref=master"
  name           = local.ctx-api_mgmt_api_name
  display_name   = "Darts Gateway Context Registry API"
  api_mgmt_name  = local.ctx-api_mgmt_name
  api_mgmt_rg    = local.ctx-api_mgmt_resource_group
  product_id     = module.ctx_api_mgmt_api.product_id
  path           = local.ctx-api_base_path
  service_url    = local.ctx-url_darts_api_hostname
  protocols      = ["http", "https"]
  api_type       = "soap"
  swagger_url    = local.ctx-url_swagger
  content_format = "wsdl-link"
  revision       = "1"
  providers = {
    azurerm = azurerm.ctx-aks-sdsapps
  }
}

# Include CNP module for setting up a policy on an API
# Uses output variable from api_mgmt_api to set api_name
module "ctx_api-mgmt-api-policy" {
  source                 = "git@github.com:hmcts/cnp-module-api-mgmt-api-policy?ref=master"
  api_mgmt_name          = local.ctx-api_mgmt_name
  api_mgmt_rg            = local.ctx-api_mgmt_resource_group
  api_name               = module.ctx_api_mgmt_api.name
  api_policy_xml_content = file("${path.module}/apim-policy/context-registry-policy.xml")
  providers = {
    azurerm = azurerm.ctx-aks-sdsapps
  }
}

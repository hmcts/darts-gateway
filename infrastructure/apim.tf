locals {
  api_mgmt_name           = "sds-api-mgmt-${var.env}"
  api_mgmt_resource_group = "ss-${var.env}-network-rg"
  api_mgmt_product_name   = "${var.product}-${var.component}"
  api_mgmt_api_name       = "${var.product}-${var.component}-api"
  api_base_path           = var.product
  url_darts_api_hostname  = "https://${var.api_hostname}"
  url_swagger             = "https://raw.githubusercontent.com/hmcts/darts-gateway/master/src/main/resources/dartsService.wsdl"
}

provider "azurerm" {
  alias           = "aks-sdsapps"
  subscription_id = var.aks_subscription_id
  features {}
}

# Include CNP module for setting up an APIM product
module "api_mgmt_product" {
  source                = "git@github.com:hmcts/cnp-module-api-mgmt-product?ref=master"
  name                  = local.api_mgmt_product_name
  approval_required     = "false"
  subscription_required = "false"
  api_mgmt_name         = local.api_mgmt_name
  api_mgmt_rg           = local.api_mgmt_resource_group
  providers = {
    azurerm = azurerm.aks-sdsapps
  }
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
  service_url    = local.url_darts_api_hostname
  protocols      = ["http", "https"]
  api_type       = "soap"
  swagger_url    = local.url_swagger
  content_format = "wsdl-link"
  revision       = "1"
  providers = {
    azurerm = azurerm.aks-sdsapps
  }
}

# Include CNP module for setting up a policy on an API
# Uses output variable from api_mgmt_api to set api_name
module "api-mgmt-api-policy" {
  source                 = "git@github.com:hmcts/cnp-module-api-mgmt-api-policy?ref=master"
  api_mgmt_name          = local.api_mgmt_name
  api_mgmt_rg            = local.api_mgmt_resource_group
  api_name               = module.api_mgmt_api.name
  api_policy_xml_content = file("${path.module}/apim-policy/api-policy.xml")
  providers = {
    azurerm = azurerm.aks-sdsapps
  }
}

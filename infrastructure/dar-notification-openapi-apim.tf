locals {
  not-api_mgmt_name           = "sds-api-mgmt-${var.env}"
  not-api_mgmt_resource_group = "ss-${var.env}-network-rg"
  not-api_mgmt_product_name   = "${var.product}-${var.component}"
  not-api_mgmt_api_name       = "${var.product}-${var.component}-api-notify"
  not-api_base_path           = var.product
  not-url_darts_api_hostname  = "https://${var.api_hostname}"
  not-url_swagger     = "https://raw.githubusercontent.com/hmcts/darts-gateway/master/src/main/resources/openapi/notification.yaml"
}

provider "azurerm" {
  alias           = "not-aks-sdsapps"
  subscription_id = var.aks_subscription_id
  features {}
}

# Include CNP module for setting up an APIM product
module "not_api_mgmt_product" {
  source                = "git@github.com:hmcts/cnp-module-api-mgmt-product?ref=master"
  name                  = local.not-api_mgmt_product_name
  approval_required     = "false"
  subscription_required = "false"
  api_mgmt_name         = local.not-api_mgmt_name
  api_mgmt_rg           = local.not-api_mgmt_resource_group
  providers = {
    azurerm = azurerm.not-aks-sdsapps
  }
}

# Include CNP module for setting up an API on an APIM product
# Uses output variable from api_mgmt_product to set product_id
# content_format needs to be set to wsdl-link as specs are in WSDL format
module "not_api_mgmt_api" {
  source         = "git@github.com:hmcts/cnp-module-api-mgmt-api?ref=master"
  name           = local.not-api_mgmt_api_name
  display_name   = "Darts Gateway Notification API"
  api_mgmt_name  = local.not-api_mgmt_name
  api_mgmt_rg    = local.not-api_mgmt_resource_group
  product_id     = module.not_api_mgmt_product.product_id
  path           = local.not-api_base_path
  service_url    = local.not-url_darts_api_hostname
  protocols      = ["http", "https"]
  swagger_url    = local.not-url_swagger
  content_format = "openapi-link"
  revision       = "1"
  providers = {
    azurerm = azurerm.not-aks-sdsapps
  }
}

module "not_api-mgmt-api-policy" {
  source                 = "git@github.com:hmcts/cnp-module-api-mgmt-api-policy?ref=master"
  api_mgmt_name          = local.not-api_mgmt_name
  api_mgmt_rg            = local.not-api_mgmt_resource_group
  api_name               = module.not_api_mgmt_product.name
  api_policy_xml_content = file("${path.module}/apim-policy/dar-notification-openapi-policy.xml")
  providers = {
    azurerm = azurerm.not-aks-sdsapps
  }
}

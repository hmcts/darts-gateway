provider "azurerm" {
  alias           = "aks-sdsapps-spike"
  subscription_id = var.aks_subscription_id
  features {}
}

# Include CNP module for setting up an API on an APIM product
# Uses output variable from api_mgmt_product to set product_id
# content_format needs to be set to wsdl-link as specs are in WSDL format
module "api_mgmt_api_spike" {
  source         = "git@github.com:hmcts/cnp-module-api-mgmt-api?ref=master"
  name           = "${local.api_mgmt_api_name}-spike"
  display_name   = "Darts Gateway API - SPIKE"
  api_mgmt_name  = local.api_mgmt_name
  api_mgmt_rg    = local.api_mgmt_resource_group
  product_id     = module.api_mgmt_product.product_id
  path           = "${local.api_base_path}_spike"
  service_url    = local.url_darts_api_hostname
  protocols      = ["http", "https"]
  api_type       = "soap"
  revision       = "1"
  providers = {
    azurerm = azurerm.aks-sdsapps-spike
  }
}

# Include CNP module for setting up a policy on an API
# Uses output variable from api_mgmt_api to set api_name
module "api-mgmt-api-spike-policy" {
  source                 = "git@github.com:hmcts/cnp-module-api-mgmt-api-policy?ref=master"
  api_mgmt_name          = local.api_mgmt_name
  api_mgmt_rg            = local.api_mgmt_resource_group
  api_name               = module.api_mgmt_api_spike.name
  api_policy_xml_content = file("${path.module}/apim-policy/api-spike-policy.xml")
  providers = {
    azurerm = azurerm.aks-sdsapps
  }
}

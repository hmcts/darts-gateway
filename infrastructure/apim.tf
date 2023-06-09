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

resource "azurerm_api_management_api" "api_spike" {
  name                = "spike-api"
  resource_group_name = local.api_mgmt_resource_group
  api_management_name = local.api_mgmt_name
  revision            = "1"
  display_name        = "Spike API"
  path                = "example"
  protocols           = ["https"]
  api_type            = "soap"
}

resource "azurerm_api_management_api_schema" "example" {
  api_name            = azurerm_api_management_api.api_spike.name
  api_management_name = local.api_mgmt_name
  resource_group_name = local.api_mgmt_resource_group
  schema_id           = "example-schema"
  content_type        = "xml"
  value               = "&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;yes&quot;?&gt; &lt;xs:schema elementFormDefault=&quot;unqualified&quot; version=&quot;1.0&quot; targetNamespace=&quot;http://rt.fs.documentum.emc.com/&quot; xmlns:tns=&quot;http://rt.fs.documentum.emc.com/&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xmlns:dfs-rt=&quot;http://rt.fs.documentum.emc.com/&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot;&gt; &lt;xs:element name=&quot;ServiceException&quot; type=&quot;tns:ServiceException&quot;/&gt; &lt;xs:complexType name=&quot;ServiceException&quot;&gt; &lt;xs:sequence&gt; &lt;xs:element name=&quot;exceptionBean&quot; type=&quot;tns:DfsExceptionHolder&quot; nillable=&quot;true&quot; minOccurs=&quot;0&quot; maxOccurs=&quot;unbounded&quot;/&gt; &lt;xs:element name=&quot;message&quot; type=&quot;xs:string&quot; minOccurs=&quot;0&quot;/&gt; &lt;xs:element name=&quot;messageArgs&quot; type=&quot;xs:anyType&quot; nillable=&quot;true&quot; minOccurs=&quot;0&quot; maxOccurs=&quot;unbounded&quot;/&gt; &lt;xs:element name=&quot;messageId&quot; type=&quot;xs:string&quot; minOccurs=&quot;0&quot;/&gt; &lt;xs:element name=&quot;stackTraceAsString&quot; type=&quot;xs:string&quot; minOccurs=&quot;0&quot;/&gt; &lt;/xs:sequence&gt; &lt;/xs:complexType&gt; &lt;xs:complexType name=&quot;DfsExceptionHolder&quot;&gt; &lt;xs:sequence&gt; &lt;xs:element name=&quot;attribute&quot; type=&quot;tns:DfsAttributeHolder&quot; nillable=&quot;true&quot; minOccurs=&quot;0&quot; maxOccurs=&quot;unbounded&quot;/&gt; &lt;xs:element name=&quot;exceptionClass&quot; type=&quot;xs:string&quot; minOccurs=&quot;0&quot;/&gt; &lt;xs:element name=&quot;genericType&quot; type=&quot;xs:string&quot; minOccurs=&quot;0&quot;/&gt; &lt;xs:element name=&quot;message&quot; type=&quot;xs:string&quot; minOccurs=&quot;0&quot;/&gt; &lt;xs:element name=&quot;messageId&quot; type=&quot;xs:string&quot; minOccurs=&quot;0&quot;/&gt; &lt;xs:element name=&quot;stackTrace&quot; type=&quot;tns:StackTraceHolder&quot; nillable=&quot;true&quot; minOccurs=&quot;0&quot; maxOccurs=&quot;unbounded&quot;/&gt; &lt;/xs:sequence&gt; &lt;/xs:complexType&gt; &lt;xs:complexType name=&quot;DfsAttributeHolder&quot;&gt; &lt;xs:sequence&gt; &lt;xs:element name=&quot;name&quot; type=&quot;xs:string&quot; minOccurs=&quot;0&quot;/&gt; &lt;xs:element name=&quot;type&quot; type=&quot;xs:string&quot; minOccurs=&quot;0&quot;/&gt; &lt;xs:element name=&quot;value&quot; type=&quot;xs:string&quot; minOccurs=&quot;0&quot;/&gt; &lt;/xs:sequence&gt; &lt;/xs:complexType&gt; &lt;xs:complexType name=&quot;StackTraceHolder&quot;&gt; &lt;xs:sequence&gt; &lt;xs:element name=&quot;className&quot; type=&quot;xs:string&quot; minOccurs=&quot;0&quot;/&gt; &lt;xs:element name=&quot;fileName&quot; type=&quot;xs:string&quot; minOccurs=&quot;0&quot;/&gt; &lt;xs:element name=&quot;lineNumber&quot; type=&quot;xs:int&quot;/&gt; &lt;xs:element name=&quot;methodName&quot; type=&quot;xs:string&quot; minOccurs=&quot;0&quot;/&gt; &lt;/xs:sequence&gt; &lt;/xs:complexType&gt; &lt;/xs:schema&gt;"
}

resource "azurerm_api_management_api_operation" "add-x" {
  operation_id        = "addX"
  api_name            = "spike-api"
  api_management_name = local.api_mgmt_name
  resource_group_name = local.api_mgmt_resource_group
  display_name        = "Add X"
  method              = "POST"
  url_template        = "/?soapAction=addX"
  description         = "This can only be done by the logged in user."

  response {
    status_code = 200
  }
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

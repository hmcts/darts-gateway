locals {
  api_mgmt_name           = "sds-api-mgmt-${var.env}"
  api_mgmt_resource_group = "ss-${var.env}-network-rg"
  api_mgmt_product_name   = "${var.product}-${var.component}"
  api_mgmt_api_name       = "${var.product}-${var.component}-api"
  api_base_path           = var.product
  url_darts_api_hostname  = "https://${var.api_hostname}"
  url_swagger             = "https://raw.githubusercontent.com/hmcts/darts-gateway/master/src/main/resources/dartsService.wsdl"

  xsd_dir = "../src/main/resources/schemas"

  api_policy_vars = {
      backend-url = "https://${var.env}"
  }
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
  protocols           = ["https", "http"]
  api_type            = "soap"
}

resource "azurerm_api_management_api_schema" "darts-schema-1" {
  api_name            = azurerm_api_management_api.api_spike.name
  api_management_name = local.api_mgmt_name
  resource_group_name = local.api_mgmt_resource_group
  schema_id           = "darts-schema-1"
  content_type        = "application/vnd.ms-azure-apim.xsd+xml"
  value               = file("${local.xsd_dir}/darts-schema-1.xsd")
}

resource "azurerm_api_management_api_schema" "darts-schema-2" {
  api_name            = azurerm_api_management_api.api_spike.name
  api_management_name = local.api_mgmt_name
  resource_group_name = local.api_mgmt_resource_group
  schema_id           = "darts-schema-2"
  content_type        = "application/vnd.ms-azure-apim.xsd+xml"
  value               = file("${local.xsd_dir}/darts-schema-2.xsd")
}

resource "azurerm_api_management_api_schema" "darts-schema-5" {
  api_name            = azurerm_api_management_api.api_spike.name
  api_management_name = local.api_mgmt_name
  resource_group_name = local.api_mgmt_resource_group
  schema_id           = "darts-schema-5"
  content_type        = "application/vnd.ms-azure-apim.xsd+xml"
  value               = file("${local.xsd_dir}/darts-schema-5.xsd")
}

resource "azurerm_api_management_api_schema" "darts-schema-3" {
  api_name            = azurerm_api_management_api.api_spike.name
  api_management_name = local.api_mgmt_name
  resource_group_name = local.api_mgmt_resource_group
  schema_id           = "darts-schema-3"
  content_type        = "application/vnd.ms-azure-apim.xsd+xml"
  value               = file("${local.xsd_dir}/darts-schema-3.xsd")
}

resource "azurerm_api_management_api_schema" "darts-schema-4" {
  api_name            = azurerm_api_management_api.api_spike.name
  api_management_name = local.api_mgmt_name
  resource_group_name = local.api_mgmt_resource_group
  schema_id           = "darts-schema-4"
  content_type        = "application/vnd.ms-azure-apim.xsd+xml"
  value               = file("${local.xsd_dir}/darts-schema-4.xsd")
}

resource "azurerm_api_management_api_schema" "darts-schema-6" {
  api_name            = azurerm_api_management_api.api_spike.name
  api_management_name = local.api_mgmt_name
  resource_group_name = local.api_mgmt_resource_group
  schema_id           = "darts-schema-6"
  content_type        = "application/vnd.ms-azure-apim.xsd+xml"
  value               = file("${local.xsd_dir}/darts-schema-6.xsd")
}

resource "azurerm_api_management_api_operation" "add-document" {
  operation_id        = "add-document"
  api_name            = "spike-api"
  api_management_name = local.api_mgmt_name
  resource_group_name = local.api_mgmt_resource_group
  display_name        = "Add Document"
  method              = "POST"
  url_template        = "/?soapAction=addDocument"
  description         = "This can only be done by the logged in user."

  request {
    description       = "addDocument"
    representation {
        content_type = "text/xml"
        schema_id    = "darts-schema-6"
        type_name    = "addDocument"
      }
  }

  response {
    status_code = 200
    description = "addDocumentResponse"
    representation {
      content_type = "text/xml"
      schema_id = "darts-schema-6"
      type_name = "addDocumentResponse"
    }
  }
}

resource "azurerm_api_management_api_operation" "add-audio" {
  operation_id        = "add-audio"
  api_name            = "spike-api"
  api_management_name = local.api_mgmt_name
  resource_group_name = local.api_mgmt_resource_group
  display_name        = "Add Audio"
  method              = "POST"
  url_template        = "/?soapAction=addAudio"
  description         = ""

  request {
    description       = "addAudio"
    representation {
      content_type = "text/xml"
      schema_id    = "darts-schema-6"
      type_name    = "addAudio"
    }
  }

  response {
    status_code = 200
    description = "addAudioResponse"
    representation {
      content_type = "text/xml"
      schema_id = "darts-schema-6"
      type_name = "addAudioResponse"
    }
  }
}

resource "azurerm_api_management_api_operation" "add-case" {
  operation_id        = "add-case"
  api_name            = "spike-api"
  api_management_name = local.api_mgmt_name
  resource_group_name = local.api_mgmt_resource_group
  display_name        = "Add Case"
  method              = "POST"
  url_template        = "/?soapAction=addCase"
  description         = ""

  request {
    description       = "addCase"
    representation {
      content_type = "text/xml"
      schema_id    = "darts-schema-6"
      type_name    = "addCase"
    }
  }

  response {
    status_code = 200
    description = "addCaseResponse"
    representation {
      content_type = "text/xml"
      schema_id = "darts-schema-6"
      type_name = "addCaseResponse"
    }
  }
}

resource "azurerm_api_management_api_operation" "add-log-entry" {
  operation_id        = "add-log-entry"
  api_name            = "spike-api"
  api_management_name = local.api_mgmt_name
  resource_group_name = local.api_mgmt_resource_group
  display_name        = "Add Log Entry"
  method              = "POST"
  url_template        = "/?soapAction=addLogEntry"
  description         = ""

  request {
    description       = "addLogEntry"
    representation {
      content_type = "text/xml"
      schema_id    = "darts-schema-6"
      type_name    = "addLogEntry"
    }
  }

  response {
    status_code = 200
    description = "addLogEntryResponse"
    representation {
      content_type = "text/xml"
      schema_id = "darts-schema-6"
      type_name = "addLogEntryResponse"
    }
  }
}

resource "azurerm_api_management_api_operation" "get-cases" {
  operation_id        = "get-cases"
  api_name            = "spike-api"
  api_management_name = local.api_mgmt_name
  resource_group_name = local.api_mgmt_resource_group
  display_name        = "Get Cases"
  method              = "POST"
  url_template        = "/?soapAction=getCases"
  description         = ""

  request {
    description       = "getCases"
    representation {
      content_type = "text/xml"
      schema_id    = "darts-schema-6"
      type_name    = "getCases"
    }
  }

  response {
    status_code = 200
    description = "getCasesResponse"
    representation {
      content_type = "text/xml"
      schema_id = "darts-schema-6"
      type_name = "getCasesResponse"
    }
  }
}

resource "azurerm_api_management_api_operation" "get-court-log" {
  operation_id        = "get-court-log"
  api_name            = "spike-api"
  api_management_name = local.api_mgmt_name
  resource_group_name = local.api_mgmt_resource_group
  display_name        = "Get Court Log"
  method              = "POST"
  url_template        = "/?soapAction=getCourtLog"
  description         = ""

  request {
    description       = "getCourtLog"
    representation {
      content_type = "text/xml"
      schema_id    = "darts-schema-6"
      type_name    = "getCourtLog"
    }
  }

  response {
    status_code = 200
    description = "getCourtLogResponse"
    representation {
      content_type = "text/xml"
      schema_id = "darts-schema-6"
      type_name = "getCourtLogResponse"
    }
  }
}

resource "azurerm_api_management_api_operation" "register-node" {
  operation_id        = "register-node"
  api_name            = "spike-api"
  api_management_name = local.api_mgmt_name
  resource_group_name = local.api_mgmt_resource_group
  display_name        = "Registers a node"
  method              = "POST"
  url_template        = "/?soapAction=registerNode"
  description         = ""

  request {
    description       = "registerNode"
    representation {
      content_type = "text/xml"
      schema_id    = "darts-schema-6"
      type_name    = "registerNode"
    }
  }

  response {
    status_code = 200
    description = "registerNodeResponse"
    representation {
      content_type = "text/xml"
      schema_id = "darts-schema-6"
      type_name = "registerNodeResponse"
    }
  }
}

resource "azurerm_api_management_api_operation" "request-transcription" {
  operation_id        = "request-transcription"
  api_name            = "spike-api"
  api_management_name = local.api_mgmt_name
  resource_group_name = local.api_mgmt_resource_group
  display_name        = "Request Transcription"
  method              = "POST"
  url_template        = "/?soapAction=requestTranscription"
  description         = ""

  request {
    description       = "requestTranscription"
    representation {
      content_type = "text/xml"
      schema_id    = "darts-schema-6"
      type_name    = "requestTranscription"
    }
  }

  response {
    status_code = 200
    description = "requestTranscriptionResponse"
    representation {
      content_type = "text/xml"
      schema_id = "darts-schema-6"
      type_name = "requestTranscriptionResponse"
    }
  }
}

resource "azurerm_api_management_api_policy" "api-policy" {
  api_name            = azurerm_api_management_api.api_spike.name
  api_management_name = local.api_mgmt_name
  resource_group_name = local.api_mgmt_resource_group

  xml_content         = file("${path.module}/apim-policy/api-spike-policy.xml")
}

resource "azurerm_api_management_api_operation_policy" "add-document-policy" {
  api_name            = azurerm_api_management_api.api_spike.name
  api_management_name = local.api_mgmt_name
  resource_group_name = local.api_mgmt_resource_group
  operation_id        = azurerm_api_management_api_operation.add-document.operation_id

  xml_content = templatefile("${path.module}/apim-policy/add-document-policy.xml", local.api_policy_vars)
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

################################################################

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

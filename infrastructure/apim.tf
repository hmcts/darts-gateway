provider "azurerm" {
  alias           = "aks-sdsapps"
  subscription_id = var.aks_subscription_id
  features {}
}

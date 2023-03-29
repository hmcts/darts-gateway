# Declare azurerm provider.  Uses AKS subscription id which is automatically set for the deployment environment by pipeline.
provider "azurerm" {
  subscription_id            = var.aks_subscription_id
  skip_provider_registration = "true"
  features {}
}

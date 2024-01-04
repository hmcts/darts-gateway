variable "product" {
  type        = string
  description = "The value of the product variable that is defined in the Jenkinsfile.  Set by pipeline."
}

variable "component" {
  type        = string
  description = "The value of the component variable that is defined in the Jenkinsfile.  Set by pipeline."
}

variable "env" {
  type        = string
  description = "The environment that the build is being run in.  Set by pipeline."
}

variable "subscription" {
  type        = string
  description = "The type of environment the build is being run in.  Default values are prod, nonprod or qa.  Set by pipeline."
}

variable "common_tags" {
  type        = map(string)
  description = "A set of common variables that can be used in the configuration.  Set by pipeline."
}

variable "aks_subscription_id" {
  type        = string
  description = "The AKS subscription id for the environment.  Set by pipeline."
}

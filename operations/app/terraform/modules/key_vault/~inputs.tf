variable "environment" {}
variable "resource_group" {}
variable "resource_prefix" {}
variable "location" {}
variable "aad_object_keyvault_admin" {}
variable "terraform_caller_ip_address" {
  type = list(string)
}
variable "use_cdc_managed_vnet" {
  type = bool
}
variable "public_subnet" {}
variable "container_subnet" {}
variable "endpoint_subnet" {}
variable "cyberark_ip_ingress" {}
variable "terraform_object_id" {}
variable "app_config_kv_name" {}
variable "application_kv_name" {}
variable "dns_vnet" {

}
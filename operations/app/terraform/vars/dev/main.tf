## Set up our Azure Virtual Network.
## Need to determine a way to run or not if vnets are pre-configured
module "vnet" {
  source          = "../../modules/vnet"
  resource_group  = var.resource_group
  environment     = var.environment
  resource_prefix = var.resource_prefix
}

##########
## 01-network
##########

module "network" {
  source             = "../../modules/network"
  vnet_address_space = module.vnet.vnet_address_spaces
  vnet_ids           = module.vnet.ids
  vnets              = module.vnet.vnets
  vnet_names         = module.vnet.names
  environment        = var.environment
  resource_group     = var.resource_group
  resource_prefix    = var.resource_prefix
  location           = var.location
}

module "nat_gateway" {
  source           = "../../modules/nat_gateway"
  environment      = var.environment
  resource_group   = var.resource_group
  resource_prefix  = var.resource_prefix
  location         = var.location
  public_subnet_id = module.network.public_subnet_ids[0]
}


##########
## 02-config
##########

module "app_service_plan" {
  source          = "../../modules/app_service_plan"
  environment     = var.environment
  resource_group  = var.resource_group
  resource_prefix = var.resource_prefix
  location        = var.location
  app_tier        = var.app_tier
  app_size        = var.app_size
}

# module "key_vault" {
#   source                      = "../../modules/key_vault"
#   environment                 = var.environment
#   resource_group              = var.resource_group
#   resource_prefix             = var.resource_prefix
#   location                    = var.location
#   aad_object_keyvault_admin   = var.aad_object_keyvault_admin
#   terraform_caller_ip_address = var.terraform_caller_ip_address
#   use_cdc_managed_vnet        = var.use_cdc_managed_vnet
#   public_subnet = module.network.public_subnet_ids
#   container_subnet = module.network.container_subnet_ids
#   endpoint_subnet = module.network.endpoint_subnet_ids
#   cyberark_ip_ingress = ""
#   terraform_object_id = var.terraform_object_id
# }

# module "container_registry" {
#   source               = "../../modules/container_registry"
#   environment          = var.environment
#   resource_group       = var.resource_group
#   resource_prefix      = var.resource_prefix
#   location             = var.location
#   enable_content_trust = true
#   public_subnets = module.network.public_subnet_ids
# }



# ##########
# ## 03-Persistent
# ##########

# module "database" {
#   source                   = "../../modules/database"
#   environment              = var.environment
#   resource_group           = var.resource_group
#   resource_prefix          = var.resource_prefix
#   location                 = var.location
#   rsa_key_2048             = var.rsa_key_2048
#   aad_group_postgres_admin = var.aad_group_postgres_admin
#   is_metabase_env          = var.is_metabase_env
#   use_cdc_managed_vnet     = var.use_cdc_managed_vnet
#   postgres_user            = data.azurerm_key_vault_secret.postgres_user.value
#   postgres_pass            = data.azurerm_key_vault_secret.postgres_pass.value
#   db_sku_name              = var.db_sku_name
#   db_version               = var.db_version
#   db_storage_mb            = var.db_storage_mb
#   db_auto_grow             = var.db_auto_grow
#   db_prevent_destroy       = var.db_prevent_destroy
#   db_threat_detection      = var.db_threat_detection
#   endpoint_subnet = module.network.endpoint_subnet_ids
#   db_replica =  var.db_replica
#   application_key_vault_id = module.key_vault.application_key_vault_id
#   west_vnet_subnets        = module.vnet.west_vnet_subnets
#   east_vnet_subnets        = module.vnet.east_vnet_subnets
#   vnet_subnets             = module.vnet.vnet_subnets
# }

# module "storage" {
#   source                      = "../../modules/storage"
#   environment                 = var.environment
#   resource_group              = var.resource_group
#   resource_prefix             = var.resource_prefix
#   location                    = var.location
#   rsa_key_4096                = var.rsa_key_4096
#   terraform_caller_ip_address = var.terraform_caller_ip_address
#   use_cdc_managed_vnet        = var.use_cdc_managed_vnet
#   endpoint_subnet = module.network.endpoint_subnet_ids
#   public_subnet = module.network.public_subnet_ids
#   container_subnet = module.network.container_subnet_ids
#   application_key_vault_id = module.key_vault.application_key_vault_id
# }



# # ##########
# # ## 04-App
# # ##########


# module "application_insights" {
#   source          = "../../modules/application_insights"
#   environment     = var.environment
#   resource_group  = var.resource_group
#   resource_prefix = var.resource_prefix
#   location        = var.location
#   is_metabase_env = var.is_metabase_env
#   pagerduty_url   = data.azurerm_key_vault_secret.pagerduty_url.value
#   postgres_server_id = module.database.postgres_server_id
#   service_plan_id = module.app_service_plan.service_plan_id
# }

# module "function_app" {
#   source                      = "../../modules/function_app"
#   environment                 = var.environment
#   resource_group              = var.resource_group
#   resource_prefix             = var.resource_prefix
#   location                    = var.location
#   ai_instrumentation_key      = module.application_insights.instrumentation_key
#   ai_connection_string        = module.application_insights.connection_string
#   okta_base_url               = var.okta_base_url
#   okta_redirect_url           = var.okta_redirect_url
#   terraform_caller_ip_address = var.terraform_caller_ip_address
#   use_cdc_managed_vnet        = var.use_cdc_managed_vnet
#   primary_access_key       = module.storage.sa_primary_access_key
#   container_registry_login_server = module.container_registry.container_registry_login_server
#   primary_connection_string  = module.storage.sa_primary_connection_string
#   app_service_plan = module.app_service_plan.service_plan_id
#   pagerduty_url = data.azurerm_key_vault_secret.pagerduty_url.value
#   postgres_user            = data.azurerm_key_vault_secret.postgres_user.value
#   postgres_pass = data.azurerm_key_vault_secret.postgres_pass.value
#   container_registry_admin_username = module.container_registry.container_registry_admin_username
#   container_registry_admin_password = module.container_registry.container_registry_admin_password
#   public_subnet = module.network.public_subnet_ids
#   application_key_vault_id = module.key_vault.application_key_vault_id
# }

# module "front_door" {
#   source           = "../../modules/front_door"
#   environment      = var.environment
#   resource_group   = var.resource_group
#   resource_prefix  = var.resource_prefix
#   location         = var.location
#   https_cert_names = var.https_cert_names
#   is_metabase_env  = var.is_metabase_env
#   public_primary_web_endpoint = module.storage.sa_public_primary_web_endpoint
#   application_key_vault_id = module.key_vault.application_key_vault_id
# }

# module "sftp_container" {
#   count = var.environment != "prod" ? 1 : 0

#   source               = "../../modules/sftp_container"
#   environment          = var.environment
#   resource_group       = var.resource_group
#   resource_prefix      = var.resource_prefix
#   location             = var.location
#   use_cdc_managed_vnet = var.use_cdc_managed_vnet
# }

# module "metabase" {
#   count = var.is_metabase_env ? 1 : 0

#   source                 = "../../modules/metabase"
#   environment            = var.environment
#   resource_group         = var.resource_group
#   resource_prefix        = var.resource_prefix
#   location               = var.location
#   ai_instrumentation_key = module.application_insights.metabase_instrumentation_key
#   ai_connection_string   = module.application_insights.metabase_connection_string
#   use_cdc_managed_vnet   = var.use_cdc_managed_vnet
# }

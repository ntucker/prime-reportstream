locals {
  dns_zones_private = [
    "privatelink.vaultcore.azure.net",
    "privatelink.postgres.database.azure.com",
    "privatelink.blob.core.windows.net",
    "privatelink.file.core.windows.net",
    "privatelink.queue.core.windows.net",
    #"privatelink.azurecr.io",
    "privatelink.servicebus.windows.net",
    "privatelink.azurewebsites.net"
  ]

  vnet_primary_name = "${var.resource_prefix}-East-vnet"
  vnet_primary      = data.azurerm_virtual_network.vnet[local.vnet_primary_name]

  vnet_names = [
    local.vnet_primary_name,
    "${var.resource_prefix}-West-vnet",
  ]
}

data "azurerm_virtual_network" "vnet" {
  for_each            = toset(local.vnet_names)
  name                = each.value
  resource_group_name = var.resource_group
}
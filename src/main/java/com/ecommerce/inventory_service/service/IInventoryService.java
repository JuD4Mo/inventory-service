package com.ecommerce.inventory_service.service;

import java.util.List;

import com.ecommerce.inventory_service.dto.InventoryRequest;
import com.ecommerce.inventory_service.dto.InventoryResponse;

public interface IInventoryService {
  boolean isInStock(String sku, Integer quantity);
  InventoryResponse createInventory(InventoryRequest inventoryRequest);
  List<InventoryResponse> getAllInventory();
  InventoryResponse updateInventory(Long id, InventoryRequest inventoryRequest);
  void deleteInventory(Long id);
  void reduceStock(String sku, Integer redQuantity);
}

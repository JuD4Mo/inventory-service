package com.ecommerce.inventory_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.inventory_service.dto.InventoryRequest;
import com.ecommerce.inventory_service.dto.InventoryResponse;
import com.ecommerce.inventory_service.exception.ResourceNotFoundException;
import com.ecommerce.inventory_service.mapper.InventoryMapper;
import com.ecommerce.inventory_service.model.Inventory;
import com.ecommerce.inventory_service.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImp implements  IInventoryService{

  private final InventoryRepository inventoryRepository;
  private final InventoryMapper inventoryMapper;

  @Override
  @Transactional(readOnly = true)
  //Transacción: conjunto de operaciones indivisibles: o todo se confirma (Commit)
  //o todo se deshace (Rollback)
  public boolean isInStock(String sku, Integer quantity) {
    return inventoryRepository.findBySku(sku).map(
      (inventory) -> inventory.getQuantity() >= quantity
    ).orElse(false);
   }

  @Override
  @Transactional
  public InventoryResponse createInventory(InventoryRequest inventoryRequest) {
    boolean exists = inventoryRepository.existsBySku(inventoryRequest.getSku());
    if (exists){
      throw new RuntimeException("El inventario para el SKU " + inventoryRequest.getSku() + "ya existe");
    }
    
    Inventory inventory = inventoryMapper.toModel(inventoryRequest);
    Inventory savedInventory = inventoryRepository.save(inventory);

    log.info("inventario creado para el SKU: {}", savedInventory.getSku());

    return inventoryMapper.toResponse(savedInventory);
  }

  @Override
  public List<InventoryResponse> getAllInventory() {
    return inventoryRepository.findAll().stream()
            .map(inventoryMapper::toResponse)
            .toList();
  }

  @Override
  @Transactional
  public InventoryResponse updateInventory(Long id, InventoryRequest inventoryRequest) {
    Inventory inventory = inventoryRepository.findById(id)
    .orElseThrow(()-> new ResourceNotFoundException("Inventario", "id", id));
    
    inventory.setSku(inventoryRequest.getSku());
    inventory.setQuantity(inventoryRequest.getQuantity());
    
    Inventory updatedInventory = inventoryRepository.save(inventory);

    log.info("Inventario actualizado para el ID: {}", id); 

    return inventoryMapper.toResponse(updatedInventory);
  }

  @Override
  @Transactional
  public void deleteInventory(Long id) {
    if (!inventoryRepository.existsById(id)){
      throw new ResourceNotFoundException("inventario", "id", id);
    }

    inventoryRepository.deleteById(id);

    log.info("Inventario eliminado con el ID: {}", id);
  }
  
}

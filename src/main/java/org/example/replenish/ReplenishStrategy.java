package org.example.replenish;

import org.example.inventory.InventoryManager;

import java.util.Map;

public interface ReplenishStrategy {
 public void replenish(InventoryManager inventoryManager, Map<Integer,Integer> itemsToReplenish);
}

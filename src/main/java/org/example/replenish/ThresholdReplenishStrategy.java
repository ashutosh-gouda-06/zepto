package org.example.replenish;

import org.example.inventory.InventoryManager;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class ThresholdReplenishStrategy implements ReplenishStrategy {

    private int threshold;


    @Override
    public void replenish(InventoryManager inventoryManager, Map<Integer, Integer> itemsToReplenish) {
        System.out.println("Replenishing items with threshold: " + threshold);
        for (Map.Entry<Integer, Integer> entry : itemsToReplenish.entrySet()) {
            int sku = entry.getKey();
            int quantity = entry.getValue();
            int currentStock = inventoryManager.checkStock(sku);
            if (currentStock < threshold) {
                inventoryManager.addStock(sku, quantity);
                System.out.println("Replenished " + quantity + " units of SKU: " + sku);
            } else {
                System.out.println("No replenishment needed for SKU: " + sku + ". Current stock: " + currentStock);
            }
        }
    }
}

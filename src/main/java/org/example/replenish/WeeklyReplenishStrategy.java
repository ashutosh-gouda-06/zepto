package org.example.replenish;

import org.example.inventory.InventoryManager;

import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
public class WeeklyReplenishStrategy implements ReplenishStrategy {
    @Override
    public void replenish(InventoryManager inventoryManager, Map<Integer, Integer> itemsToReplenish) {
        System.out.println("Replenishing items on a weekly basis.");
        for (Map.Entry<Integer, Integer> entry : itemsToReplenish.entrySet()) {
            int sku = entry.getKey();
            int quantity = entry.getValue();
            inventoryManager.addStock(sku, quantity);
            System.out.println("Replenished " + quantity + " units of SKU: " + sku);
        }
    }

}

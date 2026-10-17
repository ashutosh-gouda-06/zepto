package org.example.inventory;

import org.example.factory.ProductFactory;
import org.example.model.Product;

import java.util.List;

public record InventoryManager(InventoryStore inventoryStore) {

    public void addStock(int sku, int quantity) {
        Product product = ProductFactory.createProduct(sku);
        inventoryStore.addProduct(product, quantity);
        System.out.println("Added " + quantity + " units of " + product.getName() + " to inventory.");
    }

    public void removeStock(int sku, int quantity) {
        inventoryStore.removeProduct(sku, quantity);
    }

    public int checkStock(int sku) {
        return inventoryStore.checkStock(sku);
    }

    public List<Product> getAvailableProducts() {
        return inventoryStore.listAvailableProducts();
    }
}

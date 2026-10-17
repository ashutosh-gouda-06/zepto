package org.example.inventory;

import org.example.model.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbInventoryStore implements InventoryStore {
    private Map<Integer,Integer> stock; //SKU -> quantity
    private Map<Integer,Product> products; //SKU -> Product

    public DbInventoryStore() {
        stock = new HashMap<>();
        products = new HashMap<>();
    }

    @Override
    public void addProduct(Product product, int quantity) {
        // Implementation for adding product to the database
        int sku = product.getSku();
        if(!products.containsKey(sku)) {
            products.put(sku, product);
        }

        stock.put(sku, stock.getOrDefault(sku, 0) + quantity);

    }

    @Override
    public void removeProduct(int sku, int quantity) {
        // Implementation for removing product from the database
        if(!stock.containsKey(sku)) {
            return;
        }
        int currentStock = stock.get(sku);
        if(currentStock <= quantity) {
            stock.remove(sku);
            products.remove(sku);
        } else {
            stock.put(sku, currentStock - quantity);
        }
    }

    @Override
    public int checkStock(int sku) {
        // Implementation for checking stock in the database
        return stock.getOrDefault(sku, 0);

    }

    @Override
    public List<Product> listAvailableProducts() {
        // Implementation for listing available products from the database
        List<Product> availableProducts = products.values().stream()
                .filter(product -> stock.getOrDefault(product.getSku(), 0) > 0)
                .toList();
        return availableProducts;
    }
}

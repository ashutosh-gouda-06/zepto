package org.example.inventory;

import org.example.model.Product;

import java.util.List;

public interface InventoryStore {
     void addProduct(Product product, int quantity);
     void removeProduct(int sku, int quantity);
     int checkStock(int sku) ;
     List<Product> listAvailableProducts();

}

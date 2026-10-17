package org.example.catalog;

import org.example.model.Product;
import org.example.order.OrderManager;
import org.example.store.DarkStore;
import org.example.store.DarkStoreManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CatalogService {
    private final DarkStoreManager darkStoreManager;

    public CatalogService(DarkStoreManager darkStoreManager) {
        this.darkStoreManager = darkStoreManager;
    }

    // Distinct products in stock at any dark store within delivery range, closest store's listing first
    public List<Product> getAvailableProducts(double userX, double userY) {
        Map<Integer,Product> skuToProduct = new LinkedHashMap<>();
        for (DarkStore ds : darkStoreManager.getNearbyDarkStores(userX, userY, OrderManager.MAX_DELIVERY_DISTANCE_KM)) {
            for (Product product : ds.getAllProducts()) {
                skuToProduct.putIfAbsent(product.getSku(), product);
            }
        }
        return new ArrayList<>(skuToProduct.values());
    }
}

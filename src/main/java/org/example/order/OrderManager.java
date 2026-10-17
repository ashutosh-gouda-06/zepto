package org.example.order;

import org.example.factory.ProductFactory;
import org.example.model.Cart;
import org.example.model.DeliveryPartner;
import org.example.model.Order;
import org.example.model.Pair;
import org.example.model.Product;
import org.example.model.User;
import org.example.store.DarkStore;
import org.example.store.DarkStoreManager;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class OrderManager {
    public static final double MAX_DELIVERY_DISTANCE_KM = 5.0;

    private final DarkStoreManager darkStoreManager;
    private final List<Order> orders = new CopyOnWriteArrayList<>();

    public OrderManager(DarkStoreManager darkStoreManager) {
        this.darkStoreManager = darkStoreManager;
    }

    // synchronized: check-then-remove on store stock must not interleave across requests
    public synchronized Optional<Order> placeOrder(User user, Cart cart) {
        System.out.println("\n[OrderManager] Placing Order for: " + user.name);

        // 1) Find nearby dark stores within 5 KM
        List<DarkStore> nearbyDarkStores =
                darkStoreManager.getNearbyDarkStores(user.x, user.y, MAX_DELIVERY_DISTANCE_KM);

        if (nearbyDarkStores.isEmpty()) {
            System.out.println("  No dark stores within 5 KM. Cannot fulfill order.");
            return Optional.empty();
        }

        // 2) Fulfil from the closest store if it has everything, otherwise split
        Order order = new Order(user);
        DarkStore closestStore = nearbyDarkStores.get(0);
        if (hasAllItems(closestStore, cart.getItems())) {
            fulfilFromSingleStore(order, closestStore, cart);
        } else {
            fulfilFromMultipleStores(order, nearbyDarkStores, cart);
        }

        printSummary(order);
        orders.add(order);
        return Optional.of(order);
    }

    private boolean hasAllItems(DarkStore store, List<Pair<Product,Integer>> requestedItems) {
        for (Pair<Product,Integer> item : requestedItems) {
            if (store.checkStock(item.getKey().getSku()) < item.getValue()) {
                return false;
            }
        }
        return true;
    }

    // One delivery partner required
    private void fulfilFromSingleStore(Order order, DarkStore store, Cart cart) {
        System.out.println("  All items at: " + store.getName());

        for (Pair<Product,Integer> item : cart.getItems()) {
            store.removeStock(item.getKey().getSku(), item.getValue());
            order.items.add(new Pair<>(item.getKey(), item.getValue()));
        }

        order.totalAmount = cart.getTotal();
        order.partners.add(new DeliveryPartner("Partner1"));
        System.out.println("  Assigned Delivery Partner: Partner1");
    }

    // Multiple delivery partners required
    private void fulfilFromMultipleStores(Order order, List<DarkStore> stores, Cart cart) {
        System.out.println("  Splitting order across stores...");
        Map<Integer,Integer> remaining = new HashMap<>();
        for (Pair<Product,Integer> item : cart.getItems()) {
            remaining.put(item.getKey().getSku(), item.getValue());
        }

        int partnerId = 1;
        for (DarkStore store : stores) {
            if (remaining.isEmpty()) break;
            if (takeAvailableItems(order, store, remaining)) {
                String pname = "Partner" + partnerId++;
                order.partners.add(new DeliveryPartner(pname));
                System.out.println("     Assigned: " + pname + " for " + store.getName());
            }
        }

        if (!remaining.isEmpty()) {
            System.out.println("  Could not fulfill:");
            for (Map.Entry<Integer,Integer> entry : remaining.entrySet()) {
                System.out.println("    SKU " + entry.getKey() + " x" + entry.getValue());
            }
        }
        order.calculateTotal();
    }

    // Takes whatever the store can supply of the remaining items; returns true if it supplied anything
    private boolean takeAvailableItems(Order order, DarkStore store, Map<Integer,Integer> remaining) {
        System.out.println("   Checking: " + store.getName());
        List<Integer> fulfilledSkus = new ArrayList<>();
        boolean supplied = false;
        for (Map.Entry<Integer,Integer> entry : remaining.entrySet()) {
            int sku = entry.getKey();
            int qtyNeeded = entry.getValue();
            int availableQty = store.checkStock(sku);
            if (availableQty <= 0) continue;

            int takenQty = Math.min(availableQty, qtyNeeded);
            store.removeStock(sku, takenQty);
            supplied = true;
            System.out.println("     " + store.getName() + " supplies SKU " + sku + " x" + takenQty);
            order.items.add(new Pair<>(ProductFactory.createProduct(sku), takenQty));

            if (qtyNeeded > takenQty) {
                entry.setValue(qtyNeeded - takenQty);
            } else {
                fulfilledSkus.add(sku);
            }
        }
        fulfilledSkus.forEach(remaining::remove);
        return supplied;
    }

    private void printSummary(Order order) {
        System.out.println("\n[OrderManager] Order #" + order.orderId + " Summary:");
        System.out.println("  User: " + order.user.name + "\n  Items:");
        for (Pair<Product,Integer> item : order.items) {
            System.out.println("    SKU " + item.getKey().getSku()
                    + " (" + item.getKey().getName() + ") x" + item.getValue()
                    + " @ ₹" + item.getKey().getPrice());
        }
        System.out.println("  Total: ₹" + order.totalAmount + "\n  Partners:");
        for (DeliveryPartner dp : order.partners) {
            System.out.println("    " + dp.name);
        }
        System.out.println();
    }

    public List<Order> getAllOrders() {
        return orders;
    }
}

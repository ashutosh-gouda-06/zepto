package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Order {
    private static final AtomicInteger nextId = new AtomicInteger(1);
    public int orderId;
    public User user;
    public List<Pair<Product,Integer>> items = new ArrayList<>();
    public List<DeliveryPartner> partners = new ArrayList<>();
    public double totalAmount;

    public Order(User u) {
        orderId = nextId.getAndIncrement();
        user    = u;
        totalAmount = 0.0;
    }

    public void calculateTotal() {
        double sum = 0;
        for (Pair<Product,Integer> it : items) {
            sum += it.getKey().getPrice() * it.getValue();
        }
        totalAmount = sum;
    }
}

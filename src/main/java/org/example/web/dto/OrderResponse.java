package org.example.web.dto;

import org.example.model.Order;

import java.util.List;

public record OrderResponse(int orderId, String userName, List<LineItem> items,
                            double totalAmount, List<String> deliveryPartners) {

    public record LineItem(int sku, String name, int quantity, double price) {
    }

    public static OrderResponse from(Order order) {
        List<LineItem> items = order.items.stream()
                .map(it -> new LineItem(it.getKey().getSku(), it.getKey().getName(),
                        it.getValue(), it.getKey().getPrice()))
                .toList();
        List<String> partners = order.partners.stream().map(dp -> dp.name).toList();
        return new OrderResponse(order.orderId, order.user.name, items, order.totalAmount, partners);
    }
}

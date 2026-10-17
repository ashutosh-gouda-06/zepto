package org.example.web;

import org.example.model.Cart;
import org.example.model.User;
import org.example.order.OrderManager;
import org.example.web.dto.OrderRequest;
import org.example.web.dto.OrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderManager orderManager;

    public OrderController(OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@RequestBody OrderRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must contain at least one item");
        }
        User user = new User(request.userName(), request.x(), request.y());
        Cart cart = user.getCart();
        for (OrderRequest.Item item : request.items()) {
            if (item.quantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be positive for SKU " + item.sku());
            }
            cart.addItem(item.sku(), item.quantity());
        }
        return orderManager.placeOrder(user, cart)
                .map(OrderResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                        "No dark stores within " + OrderManager.MAX_DELIVERY_DISTANCE_KM + " km"));
    }

    @GetMapping
    public List<OrderResponse> allOrders() {
        return orderManager.getAllOrders().stream().map(OrderResponse::from).toList();
    }
}

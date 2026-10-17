package org.example.model;

public class User {
    public String name;
    public double x, y;
    private Cart cart;  // User owns a cart

    public User(String n, double x_coord, double y_coord) {
        name = n;
        x    = x_coord;
        y    = y_coord;
        cart = new Cart();
    }

    public Cart getCart() {
        return cart;
    }
}

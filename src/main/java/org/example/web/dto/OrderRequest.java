package org.example.web.dto;

import java.util.List;

public record OrderRequest(String userName, double x, double y, List<Item> items) {
    public record Item(int sku, int quantity) {
    }
}

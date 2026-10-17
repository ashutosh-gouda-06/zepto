package org.example.web;

import org.example.catalog.CatalogService;
import org.example.model.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final CatalogService catalogService;

    public ProductController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public List<Product> availableProducts(@RequestParam double x, @RequestParam double y) {
        return catalogService.getAvailableProducts(x, y);
    }
}

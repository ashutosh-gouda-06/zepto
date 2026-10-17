package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ZeptoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listsProductsWithinRange() throws Exception {
        mockMvc.perform(get("/api/products").param("x", "1.0").param("y", "1.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].sku", containsInAnyOrder(101, 102, 103, 201)));
    }

    @Test
    void splitsOrderAcrossNearbyStores() throws Exception {
        String body = """
                {"userName":"Aditya","x":1.0,"y":1.0,
                 "items":[{"sku":101,"quantity":4},{"sku":102,"quantity":3},{"sku":103,"quantity":2}]}
                """;
        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalAmount").value(210.0))
                .andExpect(jsonPath("$.deliveryPartners", contains("Partner1", "Partner2", "Partner3")));
    }

    @Test
    void rejectsOrderWithNoStoreInRange() throws Exception {
        String body = """
                {"userName":"Far","x":100.0,"y":100.0,"items":[{"sku":101,"quantity":1}]}
                """;
        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnprocessableContent());
    }
}

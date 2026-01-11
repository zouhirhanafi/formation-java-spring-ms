package ma.ensaf.ecommerce.catalogue.controller;

import ma.ensaf.ecommerce.catalogue.model.Product;
import ma.ensaf.ecommerce.catalogue.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ⚠️ TESTS FOURNIS POUR VALIDATION (Module 7)
 *
 * Ces tests utilisent MockMvc qui sera détaillé au Module 7.
 * Utilisez-les pour VALIDER vos endpoints REST.
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void shouldGetAllProducts() throws Exception {
        // Given
        doReturn(Arrays.asList(
            Product.builder().id(1L).name("Product 1").sku("P1").price(100.0).build(),
            Product.builder().id(2L).name("Product 2").sku("P2").price(200.0).build()
        )).when(productService).findAll();

        // When & Then
        mockMvc.perform(get("/api/v1/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("Product 1"));
    }

    @Test
    void shouldGetProductById() throws Exception {
        // Given
        Product product = Product.builder().id(1L).name("Laptop").sku("LAP-001").price(999.99).build();
        doReturn(Optional.of(product)).when(productService).findById(1L);

        // When & Then
        mockMvc.perform(get("/api/v1/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Laptop"))
            .andExpect(jsonPath("$.sku").value("LAP-001"));
    }

    @Test
    void shouldReturn404WhenProductNotFound() throws Exception {
        // Given
        doReturn(Optional.empty()).when(productService).findById(999L);

        // When & Then
        mockMvc.perform(get("/api/v1/products/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateProduct() throws Exception {
        // Given
        Product product = Product.builder().id(1L).name("Laptop").sku("LAP-001").price(999.99).build();
        doReturn(product).when(productService).create(any(Product.class));

        // When & Then
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Laptop\",\"sku\":\"LAP-001\",\"price\":999.99}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        // Given
        Product updated = Product.builder().id(1L).name("Updated").sku("LAP-001").price(1099.99).build();
        doReturn(updated).when(productService).update(eq(1L), any(Product.class));

        // When & Then
        mockMvc.perform(put("/api/v1/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated\",\"sku\":\"LAP-001\",\"price\":1099.99}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        // Given
        doNothing().when(productService).deleteById(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/products/1"))
            .andExpect(status().isNoContent());
    }
}

package ma.ensaf.ecommerce.catalogue.service;

import ma.ensaf.ecommerce.catalogue.model.Product;
import ma.ensaf.ecommerce.catalogue.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ⚠️ TESTS FOURNIS POUR VALIDATION (Module 7)
 *
 * Ces tests utilisent Mockito qui sera détaillé au Module 7.
 * Utilisez-les pour VALIDER votre ProductService.
 *
 * Si tous les tests passent ✅ → Votre Service est correct !
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldFindAllProducts() {
        // Given
        List<Product> products = Arrays.asList(
            Product.builder().id(1L).name("Product 1").build(),
            Product.builder().id(2L).name("Product 2").build()
        );
        doReturn(products).when(productRepository).findAll();

        // When
        List<Product> result = productService.findAll();

        // Then
        assertThat(result).hasSize(2);
        verify(productRepository).findAll();
    }

    @Test
    void shouldFindProductById() {
        // Given
        Product product = Product.builder().id(1L).name("Laptop").build();
        doReturn(Optional.of(product)).when(productRepository).findById(1L);

        // When
        Optional<Product> result = productService.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Laptop");
    }

    @Test
    void shouldCreateProduct() {
        // Given
        Product product = Product.builder()
            .name("Laptop")
            .sku("LAP-001")
            .price(999.99)
            .build();
        doReturn(false).when(productRepository).existsBySku("LAP-001");
        doReturn(product).when(productRepository).save(product);

        // When
        Product result = productService.create(product);

        // Then
        assertThat(result).isNotNull();
        verify(productRepository).save(product);
    }

    @Test
    void shouldThrowExceptionWhenSkuAlreadyExists() {
        // Given
        Product product = Product.builder().sku("LAP-001").price(999.99).build();
        doReturn(true).when(productRepository).existsBySku("LAP-001");

        // When & Then
        assertThatThrownBy(() -> productService.create(product))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("SKU")
            .hasMessageContaining("already exists");
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNegative() {
        // Given
        Product product = Product.builder().sku("LAP-001").price(-100.0).build();

        // When & Then
        assertThatThrownBy(() -> productService.create(product))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Price");
    }

    @Test
    void shouldUpdateProduct() {
        // Given
        Product existing = Product.builder().id(1L).name("Old Name").sku("LAP-001").price(999.99).build();
        Product updates = Product.builder().name("New Name").price(1099.99).build();
        doReturn(Optional.of(existing)).when(productRepository).findById(1L);
        doReturn(existing).when(productRepository).save(existing);

        // When
        Product result = productService.update(1L, updates);

        // Then
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getPrice()).isEqualTo(1099.99);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFoundForUpdate() {
        // Given
        doReturn(Optional.empty()).when(productRepository).findById(999L);

        // When & Then
        assertThatThrownBy(() -> productService.update(999L, new Product()))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("not found");
    }

    @Test
    void shouldDeleteProduct() {
        // Given
        doReturn(true).when(productRepository).existsById(1L);

        // When
        productService.deleteById(1L);

        // Then
        verify(productRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFoundForDelete() {
        // Given
        doReturn(false).when(productRepository).existsById(999L);

        // When & Then
        assertThatThrownBy(() -> productService.deleteById(999L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("not found");
    }
}

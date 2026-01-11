package ma.ensaf.ecommerce.catalogue.service;

import ma.ensaf.ecommerce.catalogue.model.Category;
import ma.ensaf.ecommerce.catalogue.repository.CategoryRepository;
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
 * ⚠️ TESTS FOURNIS POUR VALIDATION
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void shouldFindAllCategories() {
        doReturn(Arrays.asList(
            Category.builder().id(1L).name("Cat1").build(),
            Category.builder().id(2L).name("Cat2").build()
        )).when(categoryRepository).findAll();

        List<Category> result = categoryService.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldCreateCategory() {
        Category category = Category.builder().name("Electronics").code("ELEC").build();
        doReturn(false).when(categoryRepository).existsByCode("ELEC");
        doReturn(category).when(categoryRepository).save(category);

        Category result = categoryService.create(category);

        assertThat(result).isNotNull();
        verify(categoryRepository).save(category);
    }

    @Test
    void shouldThrowExceptionWhenCodeAlreadyExists() {
        Category category = Category.builder().code("ELEC").build();
        doReturn(true).when(categoryRepository).existsByCode("ELEC");

        assertThatThrownBy(() -> categoryService.create(category))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("code");
    }

    @Test
    void shouldUpdateCategory() {
        Category existing = Category.builder().id(1L).name("Old").code("OLD").build();
        Category updates = Category.builder().name("New").build();
        doReturn(Optional.of(existing)).when(categoryRepository).findById(1L);
        doReturn(existing).when(categoryRepository).save(existing);

        Category result = categoryService.update(1L, updates);

        assertThat(result.getName()).isEqualTo("New");
    }

    @Test
    void shouldDeleteCategory() {
        doReturn(true).when(categoryRepository).existsById(1L);

        categoryService.deleteById(1L);

        verify(categoryRepository).deleteById(1L);
    }
}

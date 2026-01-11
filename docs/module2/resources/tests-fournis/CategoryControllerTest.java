package ma.ensaf.ecommerce.catalogue.controller;

import ma.ensaf.ecommerce.catalogue.model.Category;
import ma.ensaf.ecommerce.catalogue.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ⚠️ TESTS FOURNIS POUR VALIDATION
 */
@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    void shouldGetAllCategories() throws Exception {
        doReturn(Arrays.asList(
            Category.builder().id(1L).name("Cat1").code("C1").build(),
            Category.builder().id(2L).name("Cat2").code("C2").build()
        )).when(categoryService).findAll();

        mockMvc.perform(get("/api/v1/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldGetCategoryById() throws Exception {
        Category category = Category.builder().id(1L).name("Electronics").code("ELEC").build();
        doReturn(Optional.of(category)).when(categoryService).findById(1L);

        mockMvc.perform(get("/api/v1/categories/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Electronics"))
            .andExpect(jsonPath("$.code").value("ELEC"));
    }

    @Test
    void shouldReturn404WhenCategoryNotFound() throws Exception {
        doReturn(Optional.empty()).when(categoryService).findById(999L);

        mockMvc.perform(get("/api/v1/categories/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateCategory() throws Exception {
        Category category = Category.builder().id(1L).name("Electronics").code("ELEC").build();
        doReturn(category).when(categoryService).create(any(Category.class));

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Electronics\",\"code\":\"ELEC\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Electronics"));
    }

    @Test
    void shouldUpdateCategory() throws Exception {
        Category updated = Category.builder().id(1L).name("Updated").code("ELEC").build();
        doReturn(updated).when(categoryService).update(eq(1L), any(Category.class));

        mockMvc.perform(put("/api/v1/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated\",\"code\":\"ELEC\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void shouldDeleteCategory() throws Exception {
        doNothing().when(categoryService).deleteById(1L);

        mockMvc.perform(delete("/api/v1/categories/1"))
            .andExpect(status().isNoContent());
    }
}

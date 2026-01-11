package ma.ensaf.catalogue.controller;

import ma.ensaf.catalogue.domain.Product;
import ma.ensaf.catalogue.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    //GET /api/v1/products
    // Récupérer la liste de tous les produits
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    //GET /api/v1/products/{id} => GET /api/v1/products/5
    // Récupérer un produit par son ID
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable(name = "id") Long pk) {
        return productService.findById(pk)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + pk));
    }

    //POST /api/v1/products body {...}
    // Créer un nouveau produit
    @PostMapping
    // utilise le statut HTTP 201 Created à la place de 200 OK qui est par défaut
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody Product product) {
        return productService.create(product);
    }

    //PUT /api/v1/products/{id} body {...}
    // Mettre à jour un produit existant
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productService.update(id, product);
    }

    //DELETE /api/v1/products/{id}
    // Supprimer un produit par son ID
    @DeleteMapping("/{id}")
    // utilise le statut HTTP 204 No Content à la place de 200 OK qui est par défaut
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
    }

    //GET /api/v1/products/search?keyword=phone
    // Rechercher des produits par mot-clé dans le nom
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String keyword) {
        log.info("Searching products with keyword: {}", keyword);
        return productService.findByKeyword(keyword);
    }

    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        return productService.findByCategory(category);
    }
}
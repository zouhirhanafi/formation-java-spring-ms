package ma.ensaf.catalogue.repository;

import ma.ensaf.catalogue.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Méthodes fournies automatiquement par JpaRepository :
    // - save(Product)
    // - findById(Long)
    // - findAll()
    // - deleteById(Long)
    // - count()
    // - existsById(Long)
    // - etc.

    // Méthodes personnalisées (Spring génère l'implémentation !)
//    @Query("select p from Product p where p.sku = ?1")
    Optional<Product> findBySku(String sku);

//    @Query("select p from Product p where p.category = ?1")
    List<Product> findByCategory(String category);

//    @Query("select p from Product p where p.available = true")
    List<Product> findByAvailableTrue();

//    @Query("select p from Product p where p.price between ?1 and ?2")
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

//    @Query("select p from Product p where upper(p.name) like upper(concat('%', ?1, '%'))")
    List<Product> findByNameContainingIgnoreCase(String name);

    @Query("select p from Product p where upper(p.name) like upper(concat('%', ?1, '%')) " +
            "or upper(p.description) like upper(concat('%', ?1, '%')) " +
            "or upper(p.category) like upper(concat('%', ?1, '%')) " +
            "or upper(p.sku) like upper(concat('%', ?1, '%'))"
    )
    List<Product> findByKeyword(String keyword);

    //    @Query("select (count(p) > 0) from Product p where p.sku = ?1")
    boolean existsBySku(String sku);
}
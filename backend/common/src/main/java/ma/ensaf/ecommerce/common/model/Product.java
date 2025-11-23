package ma.ensaf.ecommerce.common.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Entité représentant un produit dans le catalogue
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    private Long id;
    
    private String name;
    
    private String description;
    
    private Double price;
    
    private String category;
    
    private Integer stock;
    
    private boolean available;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    /**
     * Vérifie si le produit est en rupture de stock
     */
    public boolean isOutOfStock() {
        return stock == null || stock <= 0;
    }
    
    /**
     * Vérifie si le produit est disponible à la vente
     */
    public boolean isAvailableForSale() {
        return available && !isOutOfStock();
    }
    
    /**
     * Réduit le stock d'une certaine quantité
     */
    public void reduceStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (stock < quantity) {
            throw new IllegalStateException("Insufficient stock");
        }
        this.stock -= quantity;
    }
    
    /**
     * Augmente le stock d'une certaine quantité
     */
    public void increaseStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.stock = (this.stock == null ? 0 : this.stock) + quantity;
    }
}
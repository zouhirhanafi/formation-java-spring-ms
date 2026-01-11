package ma.ensaf.catalogue.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "products")
@Getter @Setter
@ToString
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Product extends AuditedEntity<Long> {

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private Double price;

    private Integer stockQuantity;

    private String category;

    private boolean available = true;

}
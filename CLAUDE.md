# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Java Spring Boot Microservices Training Program** (62h) for 2nd year Master's students. The repository contains:

- **Training documentation** (12 modules) in `docs/`
- **Example microservices** in `backend/` (Catalogue, Order, User services)
- **Code examples** in `examples/`

The training builds a complete e-commerce platform progressively across modules, with students coming from Java 7 background (no Java 8+ experience).

## Documentation Structure

The training uses a **two-level documentation approach**:

### Essential Documentation (`*-essentiel.md`)

- Quick-start guides used during classes
- Focuses on 80% use cases
- Condensed, practical examples
- Used for: `cours-essentiel.md`, `presentation-fonctionnelle-essentiel.md`, `conception-essentiel.md`

### Complete Documentation (`*.md`)

- Detailed reference material for deepening knowledge
- Comprehensive explanations, edge cases, advanced patterns
- Used for: `cours-complet.md`, `presentation-fonctionnelle.md`, `conception.md`

**CRITICAL**: When creating or editing course materials, always respect this two-level structure. Essential docs must stay concise (700-1100 lines max).

## Target Audience Constraints

Students have **Java 7 background only**. They are discovering for the first time:

- Lambda expressions
- Streams API
- Optional
- Method references
- Functional interfaces

**DO NOT** introduce concepts before they are covered in the training progression:

- Module 1: Java 8+ fundamentals (lambda, streams, Optional, Lombok)
- Module 2: Spring Boot, JPA, REST
- Module 7: Testing with Mockito (before this, only `@DataJpaTest` repository tests)
- Module 10: Profiles and environment configuration
- Later modules: Docker, Security, Microservices patterns

## Technical Conventions

### Entity Architecture Pattern

Use the three-layer entity hierarchy with generics:

```java
// BaseEntity: ID only with generics + Persistable
@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(of = "id")  // Default: ID-based
public abstract class BaseEntity<ID> implements Persistable<ID> {
    @Id
    @GeneratedValue
    private ID id;

    @Override
    public boolean isNew() {
        return getId() == null;
    }
}

// AuditedEntity: Adds timestamps + propagates generic
@MappedSuperclass
@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AuditedEntity<ID> extends BaseEntity<ID> {
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

// Product: Standard usage with Long ID
@Entity
@Table(name = "products")
@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Product extends AuditedEntity<Long> {
    @Column(nullable = false, unique = true)
    private String sku;
    // ...
}
```

**Key points**:

- **Generics (`<ID>`)**: Allows flexible ID types (Long, UUID, etc.) - applies generics from Module 1
- **`Persistable<ID>`**: Optimizes Spring Data's INSERT vs UPDATE detection
- **`@Getter @Setter @ToString`**: Preferred over `@Data` in inheritance to avoid equals/hashCode conflicts
- **`@GeneratedValue` without strategy**: Uses AUTO (JPA chooses appropriate strategy)

**equals/hashCode Strategy**:

- Default: `@EqualsAndHashCode(of = "id")` in BaseEntity (works for most cases)
- Advanced: Override with business key when entity has natural unique identifier (sku, email, orderNumber)
- Present business key approach as optional advanced technique, not mandatory

### Lombok Usage

**Always use `@SuperBuilder`**, never `@Builder` (to support inheritance).

Standard Lombok pattern:

```java
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
```

### REST API Conventions

**Always version APIs**: `/api/v1/products` (not `/api/products`)

**Controller pattern** - Prefer simple return types over `ResponseEntity`:

```java
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody Product product) {
        return productService.create(product);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
    }
}
```

Only use `ResponseEntity` for special cases (custom headers, file downloads).

### Microservices Configuration

Each microservice runs on a different port:

```yaml
server:
  port: 8081                      # Different for each service
  servlet:
    context-path: /catalogue      # Optional prefix

spring:
  application:
    name: catalogue-service
```

Port assignments:

- Catalogue Service: 8081
- Order Service: 8082
- User Service: 8083

## Project Structure

```
formation-java-spring-ms/
├── docs/                          # Training documentation
│   ├── module1/                   # Module 1: Java 8+ fundamentals
│   │   ├── cours-essentiel.md     # Essential course
│   │   ├── cours-complet.md       # Complete course
│   │   ├── exercices.md           # Hands-on exercises
│   │   └── correction.md          # Exercise solutions
│   ├── module2/                   # Module 2: Spring Boot & JPA
│   │   └── cours-essentiel.md
│   ├── conception-essentiel.md    # Data model (simplified)
│   ├── conception.md              # Data model (complete)
│   └── ...
├── backend/                       # Microservices implementation
│   ├── common/                    # Shared models (Product, etc.)
│   ├── catalogue-service/         # Product catalog microservice
│   ├── order-service/             # Order management microservice
│   ├── user-service/              # User/Auth microservice
│   └── pom.xml                    # Parent POM
├── examples/                      # Standalone code examples
└── README.md                      # Training program overview
```

## Maven Commands

```bash
# Build entire platform
mvn clean install

# Build specific service
cd backend/catalogue-service
mvn clean package

# Run tests
mvn test

# Run tests for specific service
cd backend/catalogue-service
mvn test

# Run a single test class
mvn test -Dtest=ProductTest

# Run specific test method
mvn test -Dtest=ProductTest#shouldCreateProduct
```

## Key Architecture Principles

### Multi-Module Maven Project

The `backend/` uses Maven multi-module structure:

- **Parent POM** (`backend/pom.xml`): Defines versions, shared dependencies (Lombok, JUnit, AssertJ)
- **Common module** (`backend/common/`): Shared domain models and utilities
- **Service modules**: Each microservice (catalogue, order, user) is independent but shares common configuration

### Microservices Communication

- **Module 5+**: Services communicate via OpenFeign (REST-based)
- **Module 11+**: Redis for distributed caching
- **Module 8+**: JWT-based authentication (stateless)

### Testing Strategy

- **Module 1-2**: Repository tests with `@DataJpaTest` only
- **Module 7+**: Full test suite (Mockito for unit tests, TestContainers for integration tests, MockMvc for controllers)

**Before Module 7**: Do NOT use Mockito in examples. Only show:

```java
@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository repository;

    @Test
    void shouldSaveProduct() {
        Product product = Product.builder()
            .name("Laptop")
            .sku("LAP-001")
            .build();

        Product saved = repository.save(product);
        assertThat(saved.getId()).isNotNull();
    }
}
```

## Documentation Writing Guidelines

When creating or editing course materials:

1. **Respect progression**: Don't reference concepts not yet covered
2. **Use checkpoint pattern** for complex exercises:

   ```markdown
   ### 🎯 Partie A : Méthodes de Base (1h15)
   ...
   ⚠️ **CHECKPOINT** : Validez cette partie avant de passer à la Partie B !

   ### 🚀 Partie B : Méthodes Avancées (45min)
   ```

3. **Always include timing** for theory and practice sections
4. **Use concrete examples** over abstract explanations
5. **Include cURL examples** for all REST endpoints with correct versioning and port
6. **Present advanced techniques softly**: Use "💡 Technique Avancée" sections, emphasize they're optional

### Documentation Tone

- Target students discovering Java 8+ for the first time
- Explain "why" before "how"
- Show evolution from Java 7 to Java 8+ (e.g., loop → stream comparison)
- Use emojis sparingly: ✅ ❌ 💡 ⚠️ 🎯 🚀 for structure, not decoration

## Common Patterns

### Stream API Usage

```java
// Filter and collect
products.stream()
    .filter(p -> p.getPrice() > 100)
    .collect(Collectors.toList());

// Group by category
products.stream()
    .collect(Collectors.groupingBy(Product::getCategory));

// Calculate statistics
double avgPrice = products.stream()
    .mapToDouble(Product::getPrice)
    .average()
    .orElse(0.0);
```

### Optional Usage

```java
// Service layer
public Optional<Product> findById(Long id) {
    return repository.findById(id);
}

// Controller layer
@GetMapping("/{id}")
public Product getById(@PathVariable Long id) {
    return productService.findById(id)
        .orElseThrow(() -> new RuntimeException("Product not found"));
}
```

## Final Target Architecture

By Module 12, the platform includes:

- 3+ microservices (Catalogue, Order, User)
- API Gateway
- Config Server (centralized configuration)
- PostgreSQL (persistent storage)
- Redis (distributed cache)
- Docker Compose deployment
- JWT Security
- Circuit Breaker (Resilience4j)
- Swagger documentation

Each module builds incrementally toward this final architecture.

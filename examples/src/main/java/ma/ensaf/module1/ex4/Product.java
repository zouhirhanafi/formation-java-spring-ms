package ma.ensaf.module1.ex4;

public class Product implements IEntity {

    private Long id;
    private String designation;

    public Product(String designation) {
        this.designation = designation;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }

    public String getDesignation() {
        return designation;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", designation='" + designation + '\'' +
                '}';
    }
}

package ma.ensaf.module1.ex4;

public class Tests {

    public static void main(String[] args) {
        Repository<Student> students = new Repository<>();
        System.out.println(students.save(new Student("Alice", 20)));

        Repository<Product> products = new Repository<>();
        System.out.println(products.save(new Product("Clavier")));
        System.out.println(products.save(new Product("Souris")));
        System.out.println(products.findById(2L).map(p -> p.getDesignation()).orElse(null));
        System.out.println(products.delete(2L));
        System.out.println(products.findById(2L).map(Product::getDesignation).orElse("N/A"));
    }
}

package ma.ensaf.module1.ex1;

public class Box<T> {
    private T content;

    public Box() {}

    public Box(T content) {
        this.content = content;
    }

    public void set(T content) {
        this.content = content;
    }

    public T get() {
        return content;
    }

    public boolean isEmpty() {
        return content == null;
    }
}

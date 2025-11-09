package ma.ensaf.module1.ex1;

public class Pair<K, V> {
    // Attributs pour stocker la clé et la valeur
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public Pair<V, K> swap() {
        return new Pair<>(value, key);
    }

    public static <KEY, VALUE> Pair<KEY, VALUE> of(KEY key, VALUE value) {
        return new Pair<>(key, value);
    }

    public String toString() {
        return "(" + key + ", " + value + ")";
    }
}
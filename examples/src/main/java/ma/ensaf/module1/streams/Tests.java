package ma.ensaf.module1.streams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Tests {

    public static void main(String[] args) {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");

        // Style impératif
        List<String> result = new ArrayList<>();
        for (String name : names) {
            if (name.length() > 3) {
                result.add(name.toUpperCase());
            }
        }
        System.out.println(result);

        // Style déclaratif avec Stream
        List<String> result2 = names.stream()
                .filter(name -> name.length() > 3)
//                .map(i -> i.toUpperCase())
                .map(String::toUpperCase)
                .toList();
        System.out.println(result2);
    }
}

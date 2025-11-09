package ma.ensaf.module1;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List l = new ArrayList();
        l.add("ch1");
        l.add(12);
        String s = (String) l.get(0);
//        int a = (int) l.get(1);

        System.out.println(s);
//        System.out.println(a);

        List<String> strings = new ArrayList<>();
        strings.add("ch1");
//        strings.add(2); // X entier est rejeté
        String s1 = strings.get(0);

        // int => Integer
        // long => Long
        // double => Double
        List<Integer> ints = new ArrayList<>();
        ints.add(1);
//        ints.add("3"); // X le string est rejeté
        int i = ints.get(0);

    }
}

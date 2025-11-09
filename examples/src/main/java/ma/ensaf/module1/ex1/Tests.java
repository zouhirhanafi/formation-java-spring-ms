package ma.ensaf.module1.ex1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tests {

    static void ex1() {
        System.out.println("====== Debut ex 1");
        Box<String> bs = new Box<>();
        System.out.println("Avant initialisation : " + bs.isEmpty());
        bs.set("string");
        String s = bs.get();
        System.out.println(s);
        System.out.println("Apres initialisation : " + bs.isEmpty());

        Box<Long> bl = new Box<>();
        System.out.println("Avant initialisation : " + bl.isEmpty());
        bl.set(120L);
        long l = bl.get();
        System.out.println(l);
        System.out.println("Apres initialisation : " + bl.isEmpty());
        System.out.println("======/ Fin ex 1");
    }

    static void ex2() {
        System.out.println("====== Debut ex 2");
        Pair<String, Integer> pair = Pair.of("Age", 25);
        System.out.println(pair); // (Age, 25)
        Pair<Integer, String> swapped = pair.swap();
        System.out.println(swapped); // (25, Age)
        System.out.println("======/ Fin ex 2");
    }

    static void ex3() {
        System.out.println("====== Debut ex 3");
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

        // Trouver le premier nombre pair
        Integer firstEven = SearchUtil.findFirst(numbers, new Checker<>() {
            @Override
            public boolean test(Integer n) {
                return n % 2 == 0;
            }
        });
        System.out.println(firstEven); // 2

        // Trouver le premier nombre > 4
        Integer firstBig = SearchUtil.findFirst(numbers, new Checker<>() {
            @Override
            public boolean test(Integer n) {
                return n > 4;
            }
        });
        System.out.println(firstBig); // 5

        List<String> strings = new ArrayList<>();
        strings.add("s1");
        strings.add("s2");
        strings.add(null);
        strings.add("ensaf promo 7");
        strings.add("s4");
        String firstString = SearchUtil.findFirst(strings, new Checker<>() {
            public boolean test(String s) {
                if (s == null) {
                    return false;
                }
                return s.length() > 8;
            }
        });
        System.out.println(firstString); // ensaf promo 7
        System.out.println("======/ Fin ex 3");
    }

    public static void main(String[] args) {
        ex1();
        ex2();
        ex3();
    }
}

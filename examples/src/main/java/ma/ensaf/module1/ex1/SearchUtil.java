package ma.ensaf.module1.ex1;

import java.util.List;

public class SearchUtil {

    public static <T> T findFirst(List<T> list, Checker<T> checker) {
        if (list == null || checker == null) {
            return null;
        }
        for (T element : list) {
            if (checker.test(element)) {
                return element;
            }
        }
        return null;
    }
}

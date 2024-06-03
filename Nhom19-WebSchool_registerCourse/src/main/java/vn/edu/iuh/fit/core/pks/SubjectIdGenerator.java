package vn.edu.iuh.fit.core.pks;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SubjectIdGenerator {
    private static final String PREFIX = "4203";
    private static final int ID_LENGTH = 6;

    public static String generateUniqueId() {
        StringBuilder sb = new StringBuilder(PREFIX);

        // Tạo 6 số ngẫu nhiên không trùng lặp
        Set<Integer> set = new HashSet<>();
        Random random = new Random();
        while (set.size() < ID_LENGTH) {
            set.add(random.nextInt(10));
        }

        // Chuyển đổi set thành mảng để lặp và thêm vào StringBuilder
        for (int num : set) {
            sb.append(num);
        }

        return sb.toString();
    }
}


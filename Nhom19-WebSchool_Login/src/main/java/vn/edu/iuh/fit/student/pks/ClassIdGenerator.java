package vn.edu.iuh.fit.student.pks;

import java.util.HashMap;
import java.util.Map;

public class ClassIdGenerator {
    // Lưu trữ số lớp của mỗi môn học
    private static final Map<String, Integer> classCounts = new HashMap<>();

    // Phương thức để tạo ID cho lớp Classa
    public static synchronized String generateClassId(String subjectId) {
        // Lấy số lớp hiện tại của môn học từ bản đồ classCounts
        Integer count = classCounts.get(subjectId);
        if (count == null) {
            // Nếu chưa có số lớp nào được tạo cho môn học này, đặt count bằng 1
            count = 1;
        } else {
            // Nếu đã có số lớp được tạo cho môn học này, tăng giá trị của count lên 1
            count++;
        }

        // Lưu trữ lại số lớp mới của môn học vào bản đồ classCounts
        classCounts.put(subjectId, count);

        // Tạo ID cho lớp Class bằng cách kết hợp subjectId với số lớp
        // Số lớp sẽ được định dạng thành "01", "02",...
        return subjectId + String.format("%02d", count);
    }
}


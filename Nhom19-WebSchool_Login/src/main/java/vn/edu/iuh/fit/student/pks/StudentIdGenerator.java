package vn.edu.iuh.fit.student.pks;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class StudentIdGenerator {
    private static final int ID_LENGTH = 8;

    public static String generateUniqueId() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        String timestamp = dateFormat.format(new Date());

        // Tạo một số ngẫu nhiên từ 1000 đến 9999
        Random random = new Random();
        int randomNum = random.nextInt(9000) + 1000;

        // Kết hợp thời gian và số ngẫu nhiên để tạo ID
        String id = timestamp + randomNum;

        // Trích xuất 8 ký tự cuối cùng nếu ID dài hơn
        if (id.length() > ID_LENGTH) {
            id = id.substring(id.length() - ID_LENGTH);
        }

        return id;
    }
}

package vn.edu.iuh.fit.core.controller;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.core.dto.GradeInfoDTO;
import vn.edu.iuh.fit.core.dto.SemesterGradeInfo;
import vn.edu.iuh.fit.core.dto.StudentClassInfoDTO;
import vn.edu.iuh.fit.core.models.Response;
import vn.edu.iuh.fit.core.models.Semester;
import vn.edu.iuh.fit.core.models.Student;
import vn.edu.iuh.fit.core.services.StudentServices;
import vn.edu.iuh.fit.core.utils.Constants;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("")
public class StudentManagerController {
    @Autowired
    private StudentServices studentServices;

    @GetMapping("/{studentId}/schedule")
    public ResponseEntity<Response> getStudentClasses(@PathVariable String studentId) {
        List<StudentClassInfoDTO> studentClasses = studentServices.getStudentClassInfo(studentId);
        if (studentClasses != null && !studentClasses.isEmpty()) {
            Response response = new Response(200, "Success", studentClasses);
            return ResponseEntity.ok(response);
        } else {
            Response response = new Response(404, "Not Found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/{studentId}/grades")
    public ResponseEntity<List<GradeInfoDTO>> getGradesByStudentAndSemester(
            @PathVariable String studentId,
            @RequestParam int semesterId) {
        List<GradeInfoDTO> grades = studentServices.getGradesByStudentAndSemester(studentId, semesterId);
        return ResponseEntity.ok(grades);
    }


    @GetMapping("/{studentId}/semesters")
    public ResponseEntity<List<Semester>> getSemestersByStudentId(@PathVariable String studentId) {
        List<Semester> semesters = studentServices.findSemestersByStudentIdOrderBySemesterIdAsc(studentId);
        return ResponseEntity.ok().body(semesters);
    }

    @GetMapping("/{studentId}/semester-average")
    public ResponseEntity<SemesterGradeInfo> getSemesterAverage(
            @PathVariable String studentId,
            @RequestParam int semesterId) {
        SemesterGradeInfo semesterGradeInfo = studentServices.calculateSemesterAverage(studentId, semesterId);
        return ResponseEntity.ok(semesterGradeInfo);
    }

@PostMapping("/register-graduation")
public ResponseEntity<String> registerGraduation(@RequestBody Map<String, Object> studentInfo) {
    try {
        // Lấy thông tin sinh viên từ request body
        String studentId = (String) studentInfo.get("id");
        String studentName = (String) studentInfo.get("name");
        String dateOfBirth = (String) studentInfo.get("dateOfBirth");
        String gender = (String) studentInfo.get("gender");
        String major = (String) studentInfo.get("major");

        // Kết nối đến ActiveMQ
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        Connection connection = factory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(Constants.REGISTER_COURSE);

        MessageProducer producer = session.createProducer(destination);

        String messageContent = "Sinh viên " + studentName + " (MSSV: " + studentId + ", Giới tính: " + gender +
                ", Ngày sinh: " + dateOfBirth + ", Ngành: " + major + ") đã đăng ký xét tốt nghiệp.";

        TextMessage textMsg = session.createTextMessage(messageContent);
        producer.send(textMsg);

        // Đóng kết nối
        connection.close();

        return ResponseEntity.ok("Đăng ký xét tốt nghiệp thành công");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đăng ký xét tốt nghiệp thất bại");
    }
}

}

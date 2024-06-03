package vn.edu.iuh.fit.student.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.student.dto.StudentInfoDTO;
import vn.edu.iuh.fit.student.models.Response;
import vn.edu.iuh.fit.student.models.Student;
import vn.edu.iuh.fit.student.plugin.StudentPlugin;
import vn.edu.iuh.fit.student.services.StudentServices;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/login")
public class StudentController {

    @Autowired
    private StudentServices loginServices;

    @PostMapping("/")
    public ResponseEntity<Response> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        Student student = loginServices.checkLogin(username, password);

        if(student != null) {
            StudentInfoDTO studentInfoDTO = getStudentInfoDTO(student);
            Response response = new Response(200, "Đăng nhập thành công", studentInfoDTO);
            return ResponseEntity.ok(response);
        } else {
            Response response = new Response(404, "Không tìm thấy người dùng", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    private static StudentInfoDTO getStudentInfoDTO(Student student) {
        StudentInfoDTO studentInfoDTO = new StudentInfoDTO();
        studentInfoDTO.setId(student.getId());
        studentInfoDTO.setName(student.getName());
        studentInfoDTO.setGender(student.isGender());
        studentInfoDTO.setDateOfBirth(student.getDateOfBirth());
        studentInfoDTO.setAddress(student.getAddress());
        studentInfoDTO.setPassword(student.getPassword());
        studentInfoDTO.setCourse(student.getCourse());
        studentInfoDTO.setCompletedCredits(student.getCompletedCredits());
        studentInfoDTO.setMajor(student.getMajor());
        studentInfoDTO.setMainClass(student.getMainClass());
        studentInfoDTO.setTotalCredits(student.getTotalCredits());
        studentInfoDTO.setEmail(student.getEmail());
        return studentInfoDTO;
    }
}

package vn.edu.iuh.fit.core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.core.dto.*;
import vn.edu.iuh.fit.core.models.Class;
import vn.edu.iuh.fit.core.models.Grade;
import vn.edu.iuh.fit.core.models.Response;
import vn.edu.iuh.fit.core.models.Semester;
import vn.edu.iuh.fit.core.services.CourseServices;
import vn.edu.iuh.fit.core.services.EmailService;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import vn.edu.iuh.fit.core.utils.Constants;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private CourseServices courseServices;

    @Autowired
    private EmailService emailService;

    @GetMapping("/current-semester")
    public ResponseEntity<Semester> getCurrentSemesters() {
        Semester semester = courseServices.getCurrentSemesters();
        return ResponseEntity.ok(semester);
    }

    @GetMapping("/semesters-in-range")
    public ResponseEntity<Response> getSemestersInRange(@RequestParam String course) {
        List<Semester> semesters = courseServices.getSemestersInRange(course);
        Response response = new Response(200, "Success", semesters);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{studentId}/subjects")
    public List<SubjectDTO> getSubjectsByMajorAndStudent(
            @PathVariable String studentId,
            @RequestParam Integer major) {
        List<Object[]> results = courseServices.findSubjectsByMajorAndStudent(major, studentId);
        List<SubjectDTO> subjects = new ArrayList<>();
        for (Object[] result : results) {
            String subjectId = (String) result[0];
            Integer credits = (Integer) result[1];
            String name = (String) result[2];
            Boolean status = (Boolean) result[3];
            Double tuition = (Double) result[4];
            String parentId = (String) result[5];
            subjects.add(new SubjectDTO(subjectId, credits, name, status, tuition, parentId));
        }
        return subjects;
    }

    @GetMapping("/classes/{subjectId}")
    public ResponseEntity<List<ClassDTO>> getClassesBySubject(@PathVariable String subjectId, @RequestParam int semesterId){
        List<Class> results = courseServices.findClassesBySubjectAndSemester(subjectId, semesterId);
        List<ClassDTO> classes = new ArrayList<>();
        for (Class result : results) {
            ClassDTO classDTO = new ClassDTO();
            classDTO.setId(result.getId());
            classDTO.setName(result.getName());
            classDTO.setMaxEnrollment(result.getMaxEnrollment());
            classDTO.setTeacher(result.getTeacher());
            classDTO.setDayOfWeek(result.getDayOfWeek());
            classDTO.setLesson(result.getLesson());
            classDTO.setStartDate(result.getStartDate());
            classDTO.setEndDate(result.getEndDate());
            classDTO.setClassroom(result.getClassroom());
            classes.add(classDTO);
        }
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/classes/{classId}/students")
    public ResponseEntity<Integer> countStudentsInClass(@PathVariable String classId) {
        int count = courseServices.countStudentsInClass(classId);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/enroll")
    public ResponseEntity<String> enrollStudentToClass(@RequestBody Map<String, String> body) throws JMSException {
        String studentId = body.get("studentId");
        String classId = body.get("classId");
        String subjectName = body.get("subjectName");
        String email = body.get("email");

        double totalPrice = Double.parseDouble(body.get("totalPrice"));
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        String formattedTotalPrice = numberFormat.format(totalPrice);

        LocalDate regisDate = LocalDate.now();

        boolean result = courseServices.enrollStudentToClass(studentId, classId, regisDate);
        if (result) {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
            Connection connection = factory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(Constants.REGISTER_COURSE);

            MessageProducer producer = session.createProducer(destination);

            String success = "Xin chào sinh viên có MSSV là " + studentId + ", chúng tôi thông báo anh/chị đã đăng ký thành công môn học " + subjectName + " và bây giờ tổng công nợ của anh/chị là " + formattedTotalPrice + " VNĐ " + email;

            TextMessage textMsg = session.createTextMessage(success);
            producer.send(textMsg);

            return ResponseEntity.ok("Đăng ký môn học thành công");
        } else {
            return ResponseEntity.status(500).body("Đăng ký môn học thất bại");
        }
    }


    @GetMapping("/{studentId}/classes")
    public ResponseEntity<List<RegisteredDTO>> getStudentClasses(@PathVariable String studentId, @RequestParam int semesterId) {
        List<Object[]> results = courseServices.findStudentClasses(studentId, semesterId);
        List<RegisteredDTO> classes = new ArrayList<>();
        for (Object[] result : results) {
            RegisteredDTO classDTO = new RegisteredDTO();
            classDTO.setClassId((String) result[0]);
            classDTO.setName((String) result[1]);
            classDTO.setCredits((Integer) result[2]);
            classDTO.setTotal((Double) result[3]);

            java.sql.Date sqlDate = (java.sql.Date) result[4];
            LocalDate localDate = sqlDate.toLocalDate();
            classDTO.setRegisDate(localDate);

            classDTO.setLesson((String) result[5]);
            classDTO.setDayOfWeek((String) result[6]);
            classDTO.setClassroom((String) result[7]);

            Integer longValue = (Integer) result[8];
            Boolean booleanValue = longValue != 0;
            classDTO.setStatus(booleanValue);
            classes.add(classDTO);
        }

        return ResponseEntity.ok(classes);
    }

    @DeleteMapping("/un-enroll")
    public ResponseEntity<String> deleteStudentFromClass(@RequestParam("studentId") String studentId, @RequestParam("classId") String classId) {
        boolean result = courseServices.deleteStudentFromClass(classId, studentId);
        if (result) {
            return ResponseEntity.ok("Hủy đăng ký môn học thành công");
        } else {
            return ResponseEntity.status(500).body("Hủy đăng ký môn học thất bại");
        }
    }

    @GetMapping("/{studentId}/duplicate-schedules")
    public ResponseEntity<List<DuplicateSchedulesDTO>> findDuplicateSchedules(@PathVariable String studentId, @RequestParam int semesterId) {
        List<DuplicateSchedulesDTO> results = courseServices.findDuplicateSchedules(studentId, semesterId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{studentId}/grades")
    public ResponseEntity<List<GradeCourseDTO>> getGradesByStudentId(@PathVariable String studentId) {
        List<Object[]> results = courseServices.findGradesByStudentId(studentId);
        List<GradeCourseDTO> grades = new ArrayList<>();
        for (Object[] result : results) {
            String subjectId = (String) result[0];
            Boolean isPassed = (Boolean) result[1];
            grades.add(new GradeCourseDTO(subjectId, isPassed));
        }
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/{studentId}/subjects/again")
    public List<SubjectDTO> findSubjectsForStudent(
            @PathVariable String studentId,
            @RequestParam Integer major) {
        List<Object[]> results = courseServices.findSubjectsForStudent(major, studentId);
        List<SubjectDTO> subjects = new ArrayList<>();
        for (Object[] result : results) {
            String subjectId = (String) result[0];
            Integer credits = (Integer) result[1];
            String name = (String) result[2];
            Boolean status = (Boolean) result[3];
            Double tuition = (Double) result[4];
            String parentId = (String) result[5];
            subjects.add(new SubjectDTO(subjectId, credits, name, status, tuition, parentId));
        }
        return subjects;
    }

    @GetMapping("/{studentId}/subjects/improve")
    public List<SubjectDTO> findSubjectImprove(
            @PathVariable String studentId,
            @RequestParam Integer major) {
        List<Object[]> results = courseServices.findSubjectImprove(major, studentId);
        List<SubjectDTO> subjects = new ArrayList<>();
        for (Object[] result : results) {
            String subjectId = (String) result[0];
            Integer credits = (Integer) result[1];
            String name = (String) result[2];
            Boolean status = (Boolean) result[3];
            Double tuition = (Double) result[4];
            String parentId = (String) result[5];
            subjects.add(new SubjectDTO(subjectId, credits, name, status, tuition, parentId));
        }
        return subjects;
    }

    @DeleteMapping("/delete-grade")
    public ResponseEntity<String> deleteGradeByStudentIdAndSubjectId(@RequestParam("studentId") String studentId, @RequestParam("subjectId") String subjectId) {
        boolean result = courseServices.deleteGradeByStudentIdAndSubjectId(studentId, subjectId);
        if (result) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.status(500).body("Not OK");
        }
    }

    @DeleteMapping("/delete-class-student")
    public ResponseEntity<String> deleteStudentFromClassBySubjectId(@RequestParam("studentId") String studentId, @RequestParam("subjectId") String subjectId) {
        boolean result = courseServices.deleteStudentFromClassBySubjectId(studentId, subjectId);
        if (result) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.status(500).body("Not OK");
        }
    }


}

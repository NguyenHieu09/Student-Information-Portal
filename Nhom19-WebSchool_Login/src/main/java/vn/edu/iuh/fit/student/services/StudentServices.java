package vn.edu.iuh.fit.student.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.student.models.Student;
import vn.edu.iuh.fit.student.repositories.StudentRepository;

@Service
public class StudentServices {
    @Autowired
    private StudentRepository studentRepository;

    public Student checkLogin(String username, String password) {
        return studentRepository.findStudentByIdAndPassword(username, password).orElse(null);
    }


}

package vn.edu.iuh.fit.student.dto;

import lombok.Getter;
import lombok.Setter;
import vn.edu.iuh.fit.student.models.Major;

import java.time.LocalDate;

@Getter
@Setter
public class StudentInfoDTO {
    private String id;
    private String name;
    private boolean gender;
    private LocalDate dateOfBirth;
    private String address;
    private String password;
    private String course;
    private int completedCredits;
    private Major major;
    private String mainClass;
    private int totalCredits;
    private String email;

    public StudentInfoDTO() {
    }


}

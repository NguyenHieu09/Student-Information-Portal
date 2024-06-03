package vn.edu.iuh.fit.core.dto;

import lombok.Getter;
import lombok.Setter;
import vn.edu.iuh.fit.core.models.Major;

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
    private int ownedCredits;

    public StudentInfoDTO() {
    }


}

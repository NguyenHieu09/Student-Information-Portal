package vn.edu.iuh.fit.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StudentClassInfoDTO {
    private String subjectName;
    private String className;
    private String classCode;
    private String lesson;
    private String classroom;
    private String teacherName;
    private String dateOfWeek;
    private LocalDate startDate;
    private LocalDate endDate;
}
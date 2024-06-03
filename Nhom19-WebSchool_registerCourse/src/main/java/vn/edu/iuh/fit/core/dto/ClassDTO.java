package vn.edu.iuh.fit.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ClassDTO {
    private String id;
    private String name;
    private int maxEnrollment;
    private String teacher;
    private String dayOfWeek;
    private String lesson;
    private LocalDate startDate;
    private LocalDate endDate;
    private String classroom;
}

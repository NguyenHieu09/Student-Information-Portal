package vn.edu.iuh.fit.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class RegisteredDTO {
    private String classId;
    private String name;
    private Integer credits;
    private Double total;
    private LocalDate regisDate;
    private String lesson;
    private String dayOfWeek;
    private String classroom;
    private Boolean status;
}

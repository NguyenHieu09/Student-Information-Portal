package vn.edu.iuh.fit.core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DuplicateSchedulesDTO {
    private String classId;
    private String name;
    private String dayOfWeek;
    private String lesson;
    private int semesterId;

    public DuplicateSchedulesDTO(String classId, String name, String dayOfWeek, String lesson, int semesterId) {
        this.classId = classId;
        this.name = name;
        this.dayOfWeek = dayOfWeek;
        this.lesson = lesson;
        this.semesterId = semesterId;
    }
}

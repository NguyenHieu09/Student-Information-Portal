package vn.edu.iuh.fit.core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradeCourseDTO {
    private String subjectId;
    private Boolean isPassed;

    public GradeCourseDTO(String subjectId, Boolean isPassed) {
        this.subjectId = subjectId;
        this.isPassed = isPassed;
    }

}

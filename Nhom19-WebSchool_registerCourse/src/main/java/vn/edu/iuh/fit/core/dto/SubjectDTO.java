package vn.edu.iuh.fit.core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectDTO {
    private String subjectId;
    private Integer credits;
    private String name;
    private Boolean status;
    private Double tuition;
    private String parentId;

    public SubjectDTO(String subjectId, Integer credits, String name, Boolean status, Double tuition, String parentId) {
        this.subjectId = subjectId;
        this.credits = credits;
        this.name = name;
        this.status = status;
        this.tuition = tuition;
        this.parentId = parentId;
    }

}

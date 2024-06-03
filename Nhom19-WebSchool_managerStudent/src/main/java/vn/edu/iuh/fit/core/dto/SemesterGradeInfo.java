package vn.edu.iuh.fit.core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SemesterGradeInfo {
    private Float tbhk10;
    private Float tbhk4;
    private Float tbtl10;
    private Float tbtl4;
    private int sumRegisteredCredit;
    private int passCredit;
    private int owedCredit;
    private int totalAccumulatedCredits;
    private String rankedAcademic;
    private String rankedAcademicResult;

}

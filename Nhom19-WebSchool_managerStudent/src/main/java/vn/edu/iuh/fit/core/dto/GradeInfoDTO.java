package vn.edu.iuh.fit.core.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GradeInfoDTO {
//    private String nameSemester;
//    private String course;
    private int semesterId;
    private String classId;
    private String nameSubject;
    private int credit;
    private Float tk1;
    private Float tk2;
    private Float tk3;
    private Float tk4;
    private Float tk5;
    private Float tk6;
    private Float th1;
    private Float th2;
    private Float th3;
    private Float th4;
    private Float th5;
    private Float ck;
    private Float gk;
    private Float tbsubject;
    private Float tbh4;
    private String letterGrade;
    private String rank;
    private Boolean pass;
}
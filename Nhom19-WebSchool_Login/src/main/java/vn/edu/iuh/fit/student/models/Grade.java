package vn.edu.iuh.fit.student.models;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.iuh.fit.student.pks.GradePK;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@IdClass(GradePK.class)
@Table(name = "grade")
public class Grade {
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private Student student;
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Column(nullable = true)
    private Float tk1;
    @Column(nullable = true)
    private Float tk2;
    @Column(nullable = true)
    private Float tk3;
    @Column(nullable = true)
    private Float tk4;
    @Column(nullable = true)
    private Float tk5;
    @Column(nullable = true)
    private Float tk6;
    @Column(nullable = true)
    private Float th1;
    @Column(nullable = true)
    private Float th2;
    @Column(nullable = true)
    private Float th3;
    @Column(nullable = true)
    private Float th4;
    @Column(nullable = true)
    private Float th5;
    @Column(nullable = true)
    private Float gk;
    @Column(nullable = true)
    private Float ck;
    private Boolean isPassed;


}

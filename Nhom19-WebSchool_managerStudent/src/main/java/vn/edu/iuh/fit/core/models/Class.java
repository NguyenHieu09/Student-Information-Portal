package vn.edu.iuh.fit.core.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.edu.iuh.fit.core.pks.ClassIdGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@Table(name = "class")
public class Class {
    @Id
    @Column(name = "class_id")
    private String id;
    @Column(length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    private int maxEnrollment;

//    @OneToMany(mappedBy = "classInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Student> students;
    @OneToMany(mappedBy = "classInfo")
    private List<StudentClass> studentClasses = new ArrayList<>();

    private String teacher;
//    @ElementCollection
//    @CollectionTable(name = "class_day_of_week", joinColumns = @JoinColumn(name = "class_id"))
//    @Column(name = "day_of_week")

    private String dayOfWeek;
    private String lesson;
    private LocalDate startDate;
    private LocalDate endDate;
    private String classroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id")
    private Semester semester;


    public Class(Subject subject) {
        this.subject = subject;
        this.id = generateClassId();
        // Other constructor parameters
    }

    public Class() {
    }


    public Class(String name, Subject subject, int maxEnrollment, List<StudentClass> students, String teacher, String dayOfWeek, String lesson, LocalDate startDate, LocalDate endDate, String classroom) {
        this(subject);
        this.name = name;
        this.subject = subject;
        this.maxEnrollment = maxEnrollment;
        this.studentClasses = students;
        this.teacher = teacher;
        this.dayOfWeek = dayOfWeek;
        this.lesson = lesson;
        this.startDate = startDate;
        this.endDate = endDate;
        this.classroom = classroom;
    }

    public Class(String name, Subject subject, int maxEnrollment, List<StudentClass> students, String teacher, String dayOfWeek, String lesson, LocalDate startDate, LocalDate endDate) {
        this(subject);
        this.name = name;
        this.subject = subject;
        this.maxEnrollment = maxEnrollment;
        this.studentClasses = students;
        this.teacher = teacher;
        this.dayOfWeek = dayOfWeek;
        this.lesson = lesson;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    private String generateClassId() {
        if (subject == null || subject.getId() == null) {
            throw new IllegalStateException("Subject ID is not set");
        }
        String subjectId = subject.getId();
        return ClassIdGenerator.generateClassId(subjectId);
    }
}

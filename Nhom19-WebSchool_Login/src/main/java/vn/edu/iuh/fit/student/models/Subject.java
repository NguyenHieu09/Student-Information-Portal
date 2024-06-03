package vn.edu.iuh.fit.student.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.edu.iuh.fit.student.pks.SubjectIdGenerator;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@ToString
@Table(name = "subject")
public class Subject {
    @Id
    @Column(name = "subject_id")
    private String id;
    @Column(length = 100)
    private String name;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private Subject parent;
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Subject> prerequisites = new LinkedHashSet<>();
    private int credits;
    private double tuition;
    private boolean status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "major_id")
    private Major major;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Class> classes;

    @OneToMany(mappedBy = "subject")
    private List<Grade> grades;


    public Subject() {
        // Tạo ID duy nhất khi khởi tạo đối tượng Subject mới
        this.id = SubjectIdGenerator.generateUniqueId();
    }

}

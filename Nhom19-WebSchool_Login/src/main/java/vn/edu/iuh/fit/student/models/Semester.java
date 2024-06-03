package vn.edu.iuh.fit.student.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "semester")
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "semester_id")
    private int id;
    private String name;
    @Column(length = 100)
    private String course;
    private LocalDate startDate;
    private LocalDate endDate;
}

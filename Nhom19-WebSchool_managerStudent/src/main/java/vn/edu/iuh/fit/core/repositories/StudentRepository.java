package vn.edu.iuh.fit.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.iuh.fit.core.models.Class;
import vn.edu.iuh.fit.core.models.Semester;
import vn.edu.iuh.fit.core.models.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String> {
    @Query("SELECT sc.classInfo FROM StudentClass sc WHERE sc.student.id = :studentId")
    List<Class> findClassesByStudentId(@Param("studentId") String studentId);

    @Query("SELECT DISTINCT c.semester FROM StudentClass sc JOIN sc.classInfo c WHERE sc.student.id = :studentId ORDER BY c.semester.id ASC")
    List<Semester> findSemestersByStudentIdOrderBySemesterIdAsc(String studentId);
}
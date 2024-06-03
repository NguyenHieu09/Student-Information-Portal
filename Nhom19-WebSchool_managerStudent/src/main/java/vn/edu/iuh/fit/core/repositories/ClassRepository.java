package vn.edu.iuh.fit.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.iuh.fit.core.models.Class;

import java.util.List;

public interface ClassRepository extends JpaRepository<Class, String> {
    @Query("SELECT c FROM Class c WHERE c.subject.id = :subjectId")
    List<Class> findClassesBySubjectId(@Param("subjectId") String subjectId);

    @Query("SELECT c FROM Class c JOIN c.studentClasses sc " +
            "WHERE c.semester.id = :semesterId AND sc.student.id = :studentId " +
            "AND c.id NOT IN (SELECT g.subject.id FROM Grade g WHERE g.student.id = :studentId)")
    List<Class> findClassesNotPresentInGrade(@Param("studentId") String studentId, @Param("semesterId") int semesterId);
}
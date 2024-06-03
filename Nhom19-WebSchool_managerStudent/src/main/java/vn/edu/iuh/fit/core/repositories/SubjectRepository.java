package vn.edu.iuh.fit.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.iuh.fit.core.models.Class;
import vn.edu.iuh.fit.core.models.Subject;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, String> {
    @Query("SELECT c FROM Class c WHERE c.subject.id = :subjectId")
    List<Class> findClassesBySubjectId(@Param("subjectId") String subjectId);

}
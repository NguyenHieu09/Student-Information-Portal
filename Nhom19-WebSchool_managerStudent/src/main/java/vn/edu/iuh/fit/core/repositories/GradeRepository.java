package vn.edu.iuh.fit.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.iuh.fit.core.models.Grade;
import vn.edu.iuh.fit.core.pks.GradePK;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, GradePK> {
//    @Query("SELECT DISTINCT g FROM Grade g " +
//            "JOIN g.subject s " +
//            "JOIN s.classes c " +
//            "JOIN c.studentClasses sc " +
//            "WHERE g.student.id = :studentId AND c.semester.id = :semesterId"+
//            "AND g.student.id = sc.student.id AND c.id = sc.classInfo.id")
//    List<Grade> findGradesByStudentIdAndSemesterId(@Param("studentId") String studentId, @Param("semesterId") int semesterId);
@Query("SELECT DISTINCT g FROM Grade g " +
        "JOIN g.subject s " +
        "JOIN s.classes c " +
        "JOIN c.studentClasses sc " +
        "WHERE g.student.id = :studentId " +
        "AND c.semester.id = :semesterId " +
        "AND sc.student.id = g.student.id " +
        "AND sc.classInfo.id = c.id")
List<Grade> findGradesByStudentIdAndSemesterId(@Param("studentId") String studentId, @Param("semesterId") int semesterId);




    @Transactional
    @Modifying
    @Query("UPDATE Grade g SET g.isPassed = :isPassed WHERE g.student.id = :studentId AND g.subject.id = :subjectId")
    void updateIsPassed(@Param("studentId") String studentId, @Param("subjectId") String subjectId, @Param("isPassed") Boolean isPassed);


    @Query("SELECT g FROM Grade g " +
            "JOIN g.student s " +
            "JOIN g.subject subj " +
            "JOIN subj.classes c " +
            "join c.studentClasses cs " +
            "WHERE s.id = :studentId AND c.semester.id <= :semesterId")
    List<Grade> findGradesByStudentIdAndSemesterIdLessThan(String studentId, int semesterId);


    @Query("SELECT SUM(s.credits) AS Total " +
            "FROM Grade g " +
            "JOIN g.subject s " +
            "JOIN s.classes c " +
            "JOIN c.studentClasses sc " +
            "WHERE g.student.id = :studentId AND c.semester.id = :semesterId AND g.isPassed = true " +
            "AND g.student.id = sc.student.id AND c.id = sc.classInfo.id")
    Integer getTotalPassedCreditsByStudentIdAndSemesterId(String studentId, int semesterId);

    @Query("SELECT SUM(s.credits) AS Total " +
            "FROM Grade g " +
            "JOIN g.subject s " +
            "JOIN s.classes c " +
            "JOIN c.studentClasses sc " +
            "WHERE g.student.id = :studentId AND c.semester.id <= :semesterId AND g.isPassed = true " +
            "AND g.student.id = sc.student.id AND c.id = sc.classInfo.id")
    Integer getTotalAccumulatedCreditsByStudentIdAndSemesterId(@Param("studentId") String studentId, @Param("semesterId") int semesterId);


    @Query("SELECT SUM(s.credits) AS Total " +
            "FROM Grade g " +
            "JOIN g.subject s " +
            "JOIN s.classes c " +
            "JOIN c.studentClasses sc " +
            "WHERE g.student.id = :studentId AND c.semester.id <= :semesterId AND g.isPassed = false " +
            "AND g.student.id = sc.student.id AND c.id = sc.classInfo.id")
    Integer getTotalOwnCreditsByStudentIdAndSemesterId(String studentId, int semesterId);

}
package vn.edu.iuh.fit.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.iuh.fit.core.dto.RegisteredDTO;
import vn.edu.iuh.fit.core.models.Class;
import vn.edu.iuh.fit.core.models.Grade;

import java.time.LocalDate;
import java.util.List;

public interface ClassRepository extends JpaRepository<Class, String> {
    @Query(value = "SELECT *" +
            "FROM class " +
            "WHERE subject_id = :subject AND semester_id = :semester", nativeQuery = true)
    List<Class> findClassesBySubjectAndSemester(@Param("subject") String subject, @Param("semester") int semester);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO student_class(student_id, class_id, regis_date) VALUES (:studentId, :classId, :regisDate)", nativeQuery = true)
    int enrollStudentToClass(@Param("studentId") String studentId, @Param("classId") String classId, @Param("regisDate") LocalDate regisDate);

    @Query(value = "SELECT COUNT(*) FROM student_class WHERE class_id = :classId", nativeQuery = true)
    int countStudentsInClass(@Param("classId") String classId);

    @Query(value = "SELECT c.class_id, s.name, s.credits, s.tuition * s.credits AS total, sl.regis_date,c.lesson,c.day_of_week,c.classroom,\n" +
            "    CASE \n" +
            "        WHEN CURRENT_DATE BETWEEN c.start_date AND c.end_date THEN 1 \n" +
            "        ELSE 0 \n" +
            "    END AS status \n" +
            "FROM \n" +
            "    student_class sl \n" +
            "JOIN \n" +
            "    class c ON c.class_id = sl.class_id\n" +
            "JOIN \n" +
            "    subject s ON c.subject_id = s.subject_id \n" +
            "WHERE \n" +
            "    sl.student_id = :studentId\n" +
            "    AND c.semester_id = :semesterId", nativeQuery = true)
    List<Object[]> findStudentClasses(@Param("studentId") String studentId, @Param("semesterId") int semesterId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM student_class WHERE class_id = :classId AND student_id = :studentId", nativeQuery = true)
    int deleteStudentFromClass(@Param("classId") String classId, @Param("studentId") String studentId);

    @Query(value = "SELECT c.class_id, c.name, c.day_of_week, c.lesson, c.semester_id " +
            "FROM student_class sc " +
            "JOIN class c ON c.class_id = sc.class_id " +
            "WHERE sc.student_id = :studentId AND c.semester_id = :semesterId", nativeQuery = true)
    List<Object[]> findDuplicateSchedules(@Param("studentId") String studentId, @Param("semesterId") int semesterId);

    @Query(value = "SELECT subject_id, is_passed FROM grade WHERE student_id = :studentId AND is_passed = true", nativeQuery = true)
    List<Object[]> findGradesByStudentId(@Param("studentId") String studentId);



}
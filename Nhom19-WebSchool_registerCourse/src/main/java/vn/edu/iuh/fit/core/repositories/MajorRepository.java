package vn.edu.iuh.fit.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.iuh.fit.core.models.Major;

import java.util.List;

public interface MajorRepository extends JpaRepository<Major, Integer> {
    @Query(value = "" +
            "SELECT DISTINCT sub.subject_id, sub.credits, sub.name, sub.status, sub.tuition, sub.parent_id\n" +
            "FROM subject sub \n" +
            "JOIN student stu ON sub.major_id = stu.major_id\n" +
            "WHERE stu.major_id = :major \n" +
            "AND sub.subject_id NOT IN (\n" +
            "    SELECT c.subject_id\n" +
            "    FROM student_class sl \n" +
            "    JOIN class c ON c.class_id = sl.class_id\n" +
            "    JOIN subject s ON c.subject_id = s.subject_id\n" +
            "    WHERE student_id = :student\n" +
            ");",
            nativeQuery = true)
    List<Object[]> findSubjectsByMajorAndStudent(@Param("major") Integer major, @Param("student") String student);

    //Get danh sách học lại của sinh viên
    @Query(value = "SELECT DISTINCT sub.subject_id, sub.credits, sub.name, sub.status, sub.tuition, sub.parent_id " +
            "FROM subject sub " +
            "JOIN student stu ON sub.major_id = stu.major_id " +
            "WHERE stu.major_id = :majorId " +
            "AND sub.subject_id IN ( " +
            "   SELECT s.subject_id " +
            "   FROM grade g " +
            "   JOIN subject s ON s.subject_id = g.subject_id " +
            "   WHERE g.student_id = :studentId AND g.is_passed = FALSE " +
            ")", nativeQuery = true)
    List<Object[]> findSubjectsForStudent(@Param("majorId") Integer majorId, @Param("studentId") String studentId);

    //Get danh sách học cải thiện của sinh viên
    @Query(value = "SELECT DISTINCT sub.subject_id, sub.credits, sub.name, sub.status, sub.tuition, sub.parent_id\n" +
            "FROM subject sub\n" +
            "JOIN student stu ON sub.major_id = stu.major_id\n" +
            "WHERE stu.major_id = 3 \n" +
            "AND sub.subject_id IN (\n" +
            "  SELECT s.subject_id\n" +
            "\tFROM grade g\n" +
            "\tJOIN subject s ON s.subject_id = g.subject_id\n" +
            "\tWHERE student_id = '20046666' AND is_passed = TRUE\n" +
            ");", nativeQuery = true)
    List<Object[]> findSubjectImprove(@Param("majorId") Integer majorId, @Param("studentId") String studentId);


    //Xoá điểm theo môn và theo mã sinh viên
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM grade WHERE student_id = :studentId AND subject_id = :subjectId", nativeQuery = true)
    int deleteGradeByStudentIdAndSubjectId(@Param("studentId") String studentId, @Param("subjectId") String subjectId);

    @Modifying
    @Transactional
    @Query(value = "DELETE sc\n" +
            "FROM student_class sc\n" +
            "JOIN class c ON c.class_id = sc.class_id\n" +
            "WHERE c.subject_id = :subjectId AND sc.student_id = :studentId", nativeQuery = true)
    int deleteStudentFromClassBySubjectId(@Param("studentId") String studentId, @Param("subjectId") String subjectId);

}
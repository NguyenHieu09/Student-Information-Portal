package vn.edu.iuh.fit.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.iuh.fit.core.models.Semester;

import java.util.List;

public interface SemesterRepository extends JpaRepository<Semester, Integer> {
    @Query(value = "SELECT *\n" +
            "FROM semester\n" +
            "WHERE (\n" +
            "    (MONTH(CURRENT_DATE()) >= 5 AND SUBSTRING(course, 1, 4) = YEAR(CURRENT_DATE()))\n" +
            "    OR (MONTH(CURRENT_DATE()) < 5 AND SUBSTRING(course, 6, 9) = YEAR(CURRENT_DATE()))\n" +
            ")\n" +
            "AND (\n" +
            "    (MONTH(CURRENT_DATE()) >= 5 AND NAME = 'HK1')\n" +
            "    OR (MONTH(CURRENT_DATE()) < 5 AND NAME = 'HK2')\n" +
            ");", nativeQuery = true)
    Semester findCurrentSemesters();

    @Query(value = "" +
            "SELECT * " +
            "FROM semester " +
            "WHERE course >= :course " +
            "AND course <= YEAR(CURRENT_DATE());",
            nativeQuery = true)
    List<Semester> findSemestersInRange(@Param("course") String course);


}
package vn.edu.iuh.fit.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.core.dto.DuplicateSchedulesDTO;
import vn.edu.iuh.fit.core.dto.RegisteredDTO;
import vn.edu.iuh.fit.core.models.Class;
import vn.edu.iuh.fit.core.models.Grade;
import vn.edu.iuh.fit.core.models.Semester;
import vn.edu.iuh.fit.core.repositories.ClassRepository;
import vn.edu.iuh.fit.core.repositories.MajorRepository;
import vn.edu.iuh.fit.core.repositories.SemesterRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CourseServices {
    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private ClassRepository classRepository;

    public Semester getCurrentSemesters() {
        return semesterRepository.findCurrentSemesters();
    }

    public List<Semester> getSemestersInRange(String course) {
        return semesterRepository.findSemestersInRange(course);
    }

    public List<Object[]> findSubjectsByMajorAndStudent(Integer major, String student) {
        return majorRepository.findSubjectsByMajorAndStudent(major, student);
    }

    public List<Class> findClassesBySubjectAndSemester(String subject, int semester) {
        return classRepository.findClassesBySubjectAndSemester(subject, semester);
    }

    public boolean enrollStudentToClass(String studentId, String classId, LocalDate regisDate) {
        int rowsAffected = classRepository.enrollStudentToClass(studentId, classId, regisDate);
        return rowsAffected > 0;
    }

    public int countStudentsInClass(String classId) {
        return classRepository.countStudentsInClass(classId);
    }

    public List<Object[]> findStudentClasses(String studentId, int semesterId) {
        return classRepository.findStudentClasses(studentId, semesterId);
    }

    public boolean deleteStudentFromClass(String classId, String studentId) {
        int rowsAffected = classRepository.deleteStudentFromClass(classId, studentId);
        return rowsAffected > 0;
    }

    public List<DuplicateSchedulesDTO> findDuplicateSchedules(String studentId, int semesterId) {
        List<Object[]> results = classRepository.findDuplicateSchedules(studentId, semesterId);
        List<DuplicateSchedulesDTO> classes = new ArrayList<>();
        for (Object[] result : results) {
            String classId = (String) result[0];
            String name = (String) result[1];
            String dayOfWeek = (String) result[2];
            String lesson = (String) result[3];
            int semester = (int) result[4];
            classes.add(new DuplicateSchedulesDTO(classId, name, dayOfWeek, lesson, semester));
        }
        return classes;
    }

    public List<Object[]> findGradesByStudentId(String studentId) {
        return classRepository.findGradesByStudentId(studentId);
    }

    public List<Object[]> findSubjectsForStudent(Integer major, String student) {
        return majorRepository.findSubjectsForStudent(major, student);
    }

    public List<Object[]> findSubjectImprove(Integer major, String student) {
        return majorRepository.findSubjectImprove(major, student);
    }

    public boolean deleteGradeByStudentIdAndSubjectId(String studentId, String subjectId) {
        int rowsAffected = majorRepository.deleteGradeByStudentIdAndSubjectId(studentId, subjectId);
        return rowsAffected > 0;
    }

    public boolean deleteStudentFromClassBySubjectId(String studentId, String subjectId) {
        int rowsAffected = majorRepository.deleteStudentFromClassBySubjectId(studentId, subjectId);
        return rowsAffected > 0;
    }
}

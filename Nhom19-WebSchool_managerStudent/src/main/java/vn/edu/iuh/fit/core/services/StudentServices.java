package vn.edu.iuh.fit.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.core.dto.GradeInfoDTO;
import vn.edu.iuh.fit.core.dto.SemesterGradeInfo;
import vn.edu.iuh.fit.core.dto.StudentClassInfoDTO;
import vn.edu.iuh.fit.core.models.*;
import vn.edu.iuh.fit.core.models.Class;
import vn.edu.iuh.fit.core.repositories.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StudentServices {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private ClassRepository classRepository;

    public List<StudentClassInfoDTO> getStudentClassInfo(String studentId) {
        List<StudentClassInfoDTO> studentClassInfoList = new ArrayList<>();

        // Lấy sinh viên từ repository
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student != null) {
            // Lấy danh sách các lớp học mà sinh viên tham gia
            List<StudentClass> studentClasses = student.getStudentClasses();
            for (StudentClass studentClass : studentClasses) {
                Class aclass = studentClass.getClassInfo();
                // Tạo một đối tượng StudentClassInfoDTO và thiết lập thông tin
                StudentClassInfoDTO studentClassInfo = new StudentClassInfoDTO();
                studentClassInfo.setClassName(aclass.getName());
                studentClassInfo.setClassCode(aclass.getId());
                studentClassInfo.setLesson(aclass.getLesson());
                studentClassInfo.setClassroom(aclass.getClassroom());
                studentClassInfo.setDateOfWeek(aclass.getDayOfWeek());
                studentClassInfo.setStartDate(aclass.getStartDate());
                studentClassInfo.setEndDate(aclass.getEndDate());

                // Lấy thông tin môn học từ class
                Subject subject = aclass.getSubject();
                if (subject != null) {
                    studentClassInfo.setSubjectName(subject.getName());
                }

                // Lấy thông tin giáo viên từ class
                studentClassInfo.setTeacherName(aclass.getTeacher());

                studentClassInfoList.add(studentClassInfo);
            }
        }

        return studentClassInfoList;
    }

    public List<GradeInfoDTO> getGradesByStudentAndSemester(String studentId, int semesterId) {
        List<GradeInfoDTO> gradeInfoList = new ArrayList<>();

        // Lấy danh sách Grade từ repository
        List<Grade> grades = gradeRepository.findGradesByStudentIdAndSemesterId(studentId, semesterId);
        Student student = studentRepository.findById(studentId).orElse(null);

        // Ánh xạ thông tin từ Grade sang GradeInfoDTO
        for (Grade grade : grades) {
            GradeInfoDTO gradeInfo = new GradeInfoDTO();
            // Đảm bảo rằng thông tin học kỳ được lấy từ thực thể Class
            Class classInfo = grade.getSubject().getClasses().stream()
                    .filter(c -> c.getSemester().getId() == semesterId)
                    .findFirst().orElse(null);

            if (classInfo != null) {
                gradeInfo.setSemesterId(classInfo.getSemester().getId());
            }

            gradeInfo.setNameSubject(grade.getSubject().getName());
            gradeInfo.setCredit(grade.getSubject().getCredits());
            gradeInfo.setTk1(grade.getTk1());
            gradeInfo.setTk2(grade.getTk2());
            gradeInfo.setTk3(grade.getTk3());
            gradeInfo.setTk4(grade.getTk4());
            gradeInfo.setTk5(grade.getTk5());
            gradeInfo.setTk6(grade.getTk6());
            gradeInfo.setTh1(grade.getTh1());
            gradeInfo.setTh2(grade.getTh2());
            gradeInfo.setTh3(grade.getTh3());
            gradeInfo.setTh4(grade.getTh4());
            gradeInfo.setTh5(grade.getTh5());
            gradeInfo.setCk(grade.getCk());
            gradeInfo.setGk(grade.getGk());

            // Kiểm tra xem các điểm có tồn tại không và tính toán tương ứng
            if (grade.getCk() != null && grade.getGk() != null) {
                // Tính điểm trung bình
                gradeInfo.setTbsubject(calculateAverage(grade));

                if (gradeInfo.getCk() < 3 || gradeInfo.getGk() == 0 || gradeInfo.getTbsubject() < 4) {
                    gradeInfo.setPass(false);
                    gradeInfo.setTbsubject(0.0f);
                    gradeRepository.updateIsPassed(studentId, grade.getSubject().getId(), false);
                } else {
                    gradeInfo.setPass(true);
                    gradeRepository.updateIsPassed(studentId, grade.getSubject().getId(), true);
                }

                gradeInfo.setTbh4(convertToGrade4(gradeInfo.getTbsubject()));
                gradeInfo.setLetterGrade(convertToLetterGrade(gradeInfo.getTbsubject()));
                gradeInfo.setRank(getPerformanceRating(gradeInfo.getTbsubject()));
            } else {
                gradeInfo.setPass(false);
                gradeInfo.setTbsubject(null);
                gradeInfo.setTbh4(null);
                gradeInfo.setLetterGrade(null);
                gradeInfo.setRank(null);
            }

            // Lấy danh sách các lớp học mà sinh viên tham gia
            List<Class> studentClasses = studentRepository.findClassesByStudentId(studentId);
            // Lấy danh sách các lớp học của môn học
            List<Class> subjectClasses = subjectRepository.findClassesBySubjectId(grade.getSubject().getId());

            List<Class> commonClasses = findCommonClasses(studentClasses, subjectClasses);

            // Lấy classId từ lớp học chung đó
            if (!commonClasses.isEmpty()) {
                // Trong trường hợp này, chọn lớp đầu tiên từ danh sách
                Class selectedClass = commonClasses.get(0);
                // Lấy classId và lưu vào GradeInfoDTO
                gradeInfo.setClassId(selectedClass.getId());
            }


            gradeInfoList.add(gradeInfo);
        }

        List<Class> classes = classRepository.findClassesNotPresentInGrade(studentId, semesterId);
        for (Class classInfo : classes) {
            boolean classExists = false;
            for (GradeInfoDTO gradeInfo : gradeInfoList) {
                if (gradeInfo.getClassId().equals(classInfo.getId())) {
                    classExists = true;
                    break;
                }
            }
            if (!classExists) {
                GradeInfoDTO subjectNoGrade = new GradeInfoDTO();
                subjectNoGrade.setSemesterId(classInfo.getSemester().getId());
                subjectNoGrade.setNameSubject(classInfo.getSubject().getName());
                subjectNoGrade.setCredit(classInfo.getSubject().getCredits());
                subjectNoGrade.setClassId(classInfo.getId());
                gradeInfoList.add(subjectNoGrade);
            }}
        return gradeInfoList;
    }


    // Phương thức để tìm các lớp học chung từ hai danh sách lớp học
    private List<Class> findCommonClasses(List<Class> list1, List<Class> list2) {
        List<Class> commonClasses = new ArrayList<>();
        for (Class class1 : list1) {
            for (Class class2 : list2) {
                if (class1.getId().equals(class2.getId())) {
                    commonClasses.add(class1);
                    break;
                }
            }
        }
        return commonClasses;
    }

    private float calculateAverage(Grade grade) {
        // Lấy các điểm từ đối tượng Grade
        List<Float> tkList = Arrays.asList(grade.getTk1(), grade.getTk2(), grade.getTk3(), grade.getTk4(), grade.getTk5(), grade.getTk6());
        Float gk = grade.getGk();
        Float ck = grade.getCk();

        // Lọc ra các điểm không null
        tkList = tkList.stream().filter(Objects::nonNull).collect(Collectors.toList());

        // Tính điểm trung bình của TK
        float sumTk = 0;
        for (Float tk : tkList) {
            sumTk += tk;
        }
        float avgTk = tkList.isEmpty() ? 0 : sumTk / tkList.size();

        // Tính điểm trung bình theo tỉ lệ
        float avgGrade = avgTk * 0.2f + (gk != null ? gk * 0.3f : 0) + (ck != null ? ck * 0.5f : 0);

        DecimalFormat df = new DecimalFormat("#.##");
        return Float.parseFloat(df.format(avgGrade));
    }

    private float convertToGrade4(float avgGrade) {
        // Chuyển điểm từ hệ 10 sang hệ 4
        float grade4 = avgGrade * 4 / 10;
        // Giới hạn điểm hệ 4 trong khoảng từ 0 đến 4.0
        grade4 = Math.min(grade4, 4.0f);
        // Làm tròn điểm hệ 4 đến 2 chữ số sau dấu '.'
        DecimalFormat df = new DecimalFormat("#.##");
        return Float.parseFloat(df.format(grade4));
    }

    private String convertToLetterGrade(float avgGrade) {
        if (avgGrade >= 8.5) {
            return "A";
        } else if (avgGrade >= 7.8) {
            return "B+";
        } else if (avgGrade >= 7.0) {
            return "B";
        } else if (avgGrade >= 6.3) {
            return "C+";
        } else if (avgGrade >= 5.5) {
            return "C";
        } else if (avgGrade >= 4.8) {
            return "D+";
        } else if (avgGrade >= 4.0) {
            return "D";
        } else {
            return "F";
        }
    }

    private String getPerformanceRating(float avgGrade) {
        if (avgGrade >= 8.5) {
            return "Giỏi";
        } else if (avgGrade >= 7.8) {
            return "Khá";
        } else if (avgGrade >= 7.0) {
            return "Khá";
        } else if (avgGrade >= 6.3) {
            return "Trung bình";
        } else if (avgGrade >= 5.5) {
            return "Trung bình";
        } else if (avgGrade >= 4.8) {
            return "Trung bình yếu";
        } else if (avgGrade >= 4.0) {
            return "Trung bình yếu";
        } else {
            return "Học lại";
        }
    }


    public SemesterGradeInfo calculateSemesterAverage(String studentId, int semesterId) {
        DecimalFormat df = new DecimalFormat("#.##");

        // Lấy danh sách các điểm của sinh viên trong học kỳ
        List<Grade> grades = gradeRepository.findGradesByStudentIdAndSemesterId(studentId, semesterId);
        Student student = studentRepository.findById(studentId).orElse(null);

        // Kiểm tra nếu sinh viên không tồn tại
        if (student == null) {
            throw new IllegalArgumentException("Student not found");
        }

        // Kiểm tra nếu danh sách điểm rỗng hoặc có môn nào điểm cuối kỳ là null thì không tính trung bình học kỳ
        if (grades.isEmpty() || grades.stream().anyMatch(grade -> grade.getCk() == null)) {
            return null;
        }

        // Tiếp tục tính toán trung bình học kỳ nếu không có môn nào điểm cuối kỳ là null
        float totalWeightedGrades10 = 0;
        int totalCredits = 0;
        int passCredits = 0;
        int owedCredits = 0;

        // Tạo đối tượng SemesterGradeInfo
        SemesterGradeInfo semesterGradeInfo = new SemesterGradeInfo();

        for (Grade grade : grades) {
            int credits = grade.getSubject().getCredits();
            float averageGrade = calculateAverage(grade);  // Sử dụng phương thức calculateAverage đã có

            if (grade.getCk() < 3 || grade.getGk() == 0 || averageGrade < 4) {
                averageGrade = 0.0f;
            }
            totalWeightedGrades10 += averageGrade * credits;
            totalCredits += credits;

            if (averageGrade >= 4.0) {
                passCredits += credits;
            } else {
                owedCredits += credits;
            }
        }

        if (totalCredits > 0) {
            float semesterAverage10 = totalWeightedGrades10 / totalCredits;
            semesterGradeInfo.setTbhk10(Float.parseFloat(df.format(semesterAverage10)));
            semesterGradeInfo.setTbhk4(convertToGrade4(semesterAverage10));
        } else {
            semesterGradeInfo.setTbhk10(0.0f);
            semesterGradeInfo.setTbhk4(0.0f);
        }

        float accumulatedAverage10 = calculateAccumulatedAverage(studentId, semesterId);

        // Tính trung bình tích lũy
        semesterGradeInfo.setTbtl10(Float.parseFloat(df.format(accumulatedAverage10)));
        semesterGradeInfo.setTbtl4(convertToGrade4(accumulatedAverage10));

        // Tổng số tín chỉ đã đăng ký
        int sumRegisteredCredit = calculateSumRegisteredCredit(grades);
        semesterGradeInfo.setSumRegisteredCredit(sumRegisteredCredit);

        // Tổng số tín chỉ đã pass
        Integer totalPassedCredits = gradeRepository.getTotalPassedCreditsByStudentIdAndSemesterId(studentId, semesterId);
        semesterGradeInfo.setPassCredit(totalPassedCredits != null ? totalPassedCredits : 0);

        // Tổng số tín chỉ nợ
        Integer totalOwedCredits = gradeRepository.getTotalOwnCreditsByStudentIdAndSemesterId(studentId, semesterId);
        semesterGradeInfo.setOwedCredit(totalOwedCredits != null ? totalOwedCredits : 0);

        // Tổng số tín chỉ tích lũy
        Integer totalAccumulatedCredits = gradeRepository.getTotalAccumulatedCreditsByStudentIdAndSemesterId(studentId, semesterId);
        if (totalAccumulatedCredits != null) {
            semesterGradeInfo.setTotalAccumulatedCredits(totalAccumulatedCredits);
            student.setCompletedCredits(totalAccumulatedCredits);
            studentRepository.save(student);
        } else {
            semesterGradeInfo.setTotalAccumulatedCredits(0);
        }

        // Xếp hạng học tập dựa trên điểm trung bình học kỳ hệ 10
        semesterGradeInfo.setRankedAcademic(getPerformanceRating(semesterGradeInfo.getTbhk10()));
        // Xếp hạng kết quả học tập dựa trên điểm trung bình tích lũy hệ 10
        semesterGradeInfo.setRankedAcademicResult(getPerformanceRating(semesterGradeInfo.getTbtl10()));

        return semesterGradeInfo;
    }


    private int calculateSumRegisteredCredit(List<Grade> grades) {
        int sumRegisteredCredit = 0;
        for (Grade grade : grades) {
            if (grade.getGk() != null) {
                sumRegisteredCredit += grade.getSubject().getCredits();
            }
        }
        return sumRegisteredCredit;
    }

    private float calculateAccumulatedAverage(String studentId, int semesterId) {
        // Lấy tất cả các điểm của sinh viên từ kỳ 1 đến kỳ hiện tại
        List<Grade> allGrades = gradeRepository.findGradesByStudentIdAndSemesterIdLessThan(studentId, semesterId);

        float totalWeightedGrades = 0;
        int totalCredits = 0;

        for (Grade grade : allGrades) {
            int credits = grade.getSubject().getCredits();
            float averageGrade = calculateAverage(grade);
            if (grade.getCk() != null) {
                // Kiểm tra điểm ck có khác null
                if (grade.getCk() < 3 || grade.getGk() == 0 || averageGrade < 4) {
                    averageGrade = 0.0f;
                }
//                else {
                    totalWeightedGrades += averageGrade * credits;
                    totalCredits += credits;
//                }
            }
        }

        return totalCredits > 0 ? totalWeightedGrades / totalCredits : 0.0f;
    }


    public List<Semester> findSemestersByStudentIdOrderBySemesterIdAsc(String studentId) {
        return studentRepository.findSemestersByStudentIdOrderBySemesterIdAsc(studentId);
    }

    
}

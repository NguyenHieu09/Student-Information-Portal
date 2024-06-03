package vn.edu.iuh.fit.core.pks;

import lombok.Getter;
import lombok.Setter;
import vn.edu.iuh.fit.core.models.Class;
import vn.edu.iuh.fit.core.models.Student;

import java.io.Serializable;
import java.util.Objects;

@Getter @Setter
public class StudentClassPK implements Serializable {
    private Student student;
    private Class classInfo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentClassPK that)) return false;

        if (!Objects.equals(student, that.student)) return false;
        return Objects.equals(classInfo, that.classInfo);
    }

    @Override
    public int hashCode() {
        int result = student != null ? student.hashCode() : 0;
        result = 31 * result + (classInfo != null ? classInfo.hashCode() : 0);
        return result;
    }
}

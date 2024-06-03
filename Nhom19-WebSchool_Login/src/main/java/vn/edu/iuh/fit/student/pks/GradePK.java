package vn.edu.iuh.fit.student.pks;

import lombok.Getter;
import lombok.Setter;
import vn.edu.iuh.fit.student.models.Student;
import vn.edu.iuh.fit.student.models.Subject;

import java.io.Serializable;

@Getter @Setter
public class GradePK implements Serializable {
    private Student student;
//    private Subject subject;
    private Subject subject;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GradePK gradePK)) return false;

        if (getStudent() != null ? !getStudent().equals(gradePK.getStudent()) : gradePK.getStudent() != null)
            return false;
        return getSubject() != null ? getSubject().equals(gradePK.getSubject()) : gradePK.getSubject() == null;
    }

    @Override
    public int hashCode() {
        int result = getStudent() != null ? getStudent().hashCode() : 0;
        result = 31 * result + (getSubject() != null ? getSubject().hashCode() : 0);
        return result;
    }
}

package vn.edu.iuh.fit.student.plugin;

import vn.edu.iuh.fit.student.models.Student;

public interface StudentPlugin extends Plugin {
    Student login(String username, String password);

    String sayHello(String name);

}

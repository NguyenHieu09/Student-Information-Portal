package vn.edu.iuh.fit.student.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response {
    private int status;
    private String message;
    private Object object;

    public Response() {

    }

    public Response(int status, String message, Object object) {
        this.status = status;
        this.message = message;
        this.object = object;
    }

    @Override
    public String toString() {
        return super.toString();
    }


}

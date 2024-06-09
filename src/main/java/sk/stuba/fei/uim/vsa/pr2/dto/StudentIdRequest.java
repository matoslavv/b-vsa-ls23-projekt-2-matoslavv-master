package sk.stuba.fei.uim.vsa.pr2.dto;

import javax.validation.constraints.NotEmpty;

public class StudentIdRequest {
    @NotEmpty
    Long studentId;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public StudentIdRequest(Long studentId) {
        this.studentId = studentId;
    }

    public StudentIdRequest() {
    }
}

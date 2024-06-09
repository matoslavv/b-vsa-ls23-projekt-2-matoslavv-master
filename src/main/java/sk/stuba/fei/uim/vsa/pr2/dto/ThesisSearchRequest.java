package sk.stuba.fei.uim.vsa.pr2.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ThesisSearchRequest {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long studentId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long teacherId;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }
}

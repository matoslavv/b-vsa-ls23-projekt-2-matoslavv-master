package sk.stuba.fei.uim.vsa.pr2.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import sk.stuba.fei.uim.vsa.pr1.solution.Student;
import sk.stuba.fei.uim.vsa.pr1.solution.Thesis;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentAltResponse {
    @NotEmpty
    public Long id;
    @NotEmpty
    public Long aisId;

    public String name;

    @Email
    @NotEmpty
    public String email;
    public Integer year;
    public Integer term;
    public String programme;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Long thesis;

    public StudentAltResponse(Student student) {
        this.id = student.getAisId();
        this.aisId = student.getAisId();
        this.email = student.getEmail();

        if (student.getName() != null) {
            this.name = student.getName();
        }

        if (student.getYear() != null) {
            this.year = student.getYear();
        }

        if (student.getTerm() != null) {
            this.term = student.getTerm();
        }

        if (student.getProgramme() != null) {
            this.programme = student.getProgramme();
        }

        if (student.getThesis() != null) {
            this.thesis = student.getThesis().getId();
        }
    }
}

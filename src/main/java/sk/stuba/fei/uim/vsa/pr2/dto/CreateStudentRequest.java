package sk.stuba.fei.uim.vsa.pr2.dto;

import lombok.Data;
import sk.stuba.fei.uim.vsa.pr1.solution.Student;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class CreateStudentRequest {
    @NotEmpty
    private Long aisId;

    @NotEmpty
    private String name;

    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}")
    @NotEmpty
    private String email;

    @NotEmpty
    private String password;
    private Integer year;
    private Integer term;
    private String programme;

    public CreateStudentRequest(Student student) {
        this.aisId = student.getAisId();
        this.name = student.getName();
        this.email = student.getEmail();
        this.password = student.getPassword();

        if (student.getYear() != null) {
            this.year = student.getYear();
        }

        if (student.getTerm() != null) {
            this.term = student.getTerm();
        }

        if (student.getProgramme() != null) {
            this.programme = student.getProgramme();
        }
    }

    public CreateStudentRequest() {
    }


}

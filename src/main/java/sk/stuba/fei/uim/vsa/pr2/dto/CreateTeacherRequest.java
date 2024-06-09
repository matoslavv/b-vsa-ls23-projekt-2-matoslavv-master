package sk.stuba.fei.uim.vsa.pr2.dto;

import lombok.Data;
import sk.stuba.fei.uim.vsa.pr1.solution.Teacher;
import sk.stuba.fei.uim.vsa.pr2.BCryptService;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class CreateTeacherRequest {
    @NotEmpty
    private Long aisId;
    @NotEmpty
    private String name;
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}")
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
    private String institute;
    private String department;

    public CreateTeacherRequest(Teacher teacher) {
        this.aisId = teacher.getAisId();
        this.name = teacher.getName();
        this.email = teacher.getEmail();
        this.password = teacher.getPassword();

        if (teacher.getInstitute() != null) {
            this.institute = teacher.getInstitute();
        }

        if (teacher.getDepartment() != null) {
            this.department = teacher.getDepartment();
        }
    }

    public CreateTeacherRequest() {
    }
}

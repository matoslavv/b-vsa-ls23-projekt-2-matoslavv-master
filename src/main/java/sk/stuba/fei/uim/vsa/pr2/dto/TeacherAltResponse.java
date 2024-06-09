package sk.stuba.fei.uim.vsa.pr2.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import sk.stuba.fei.uim.vsa.pr1.solution.Teacher;
import sk.stuba.fei.uim.vsa.pr1.solution.Thesis;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

public class TeacherAltResponse {
    @NotEmpty
    public Long id;
    @NotEmpty
    public Long aisId;
    public String name;

    @Email
    @NotEmpty
    public String email;

    public String institute;
    public String department;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<Long> theses;

    public TeacherAltResponse(Teacher teacher) {
        this.id = teacher.getAisId();
        this.aisId = teacher.getAisId();
        this.email = teacher.getEmail();

        if (teacher.getName() != null) {
            this.name = teacher.getName();
        }

        if (teacher.getName() != null) {
            this.name = teacher.getName();
        }

        if (teacher.getInstitute() != null) {
            this.institute = teacher.getInstitute();
        }

        if (teacher.getDepartment() != null) {
            this.department = teacher.getDepartment();
        }

//        this.theses = new ArrayList<>();
//
        if (teacher.getSupervisedTheses() != null) {
            this.theses = new ArrayList<>();

            for (Thesis thesis : teacher.getSupervisedTheses()) {
                this.theses.add(thesis.getId());
            }
        }
    }
}

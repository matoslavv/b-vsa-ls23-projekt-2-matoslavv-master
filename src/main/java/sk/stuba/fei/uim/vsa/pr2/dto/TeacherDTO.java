/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stuba.fei.uim.vsa.pr2.dto;

import java.util.ArrayList;
import java.util.List;
import sk.stuba.fei.uim.vsa.pr1.solution.Teacher;
import sk.stuba.fei.uim.vsa.pr1.solution.Thesis;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 *
 * @author edu
 */
public class TeacherDTO {
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
    public List<ThesisDTO> theses;
    
    public TeacherDTO(Teacher t) {
        this.id = t.getAisId();
        this.aisId = t.getAisId();
        this.email = t.getEmail();

        if (t.getName() != null) {
            this.name = t.getName();
        }

        if (t.getInstitute() != null) {
            this.institute = t.getInstitute();
        }

        if (t.getDepartment() != null) {
            this.department = t.getDepartment();
        }

        this.theses = new ArrayList<>();
        
        if (t.getSupervisedTheses() != null) {
            for (Thesis thesis : t.getSupervisedTheses()) {
                this.theses.add(new ThesisDTO(thesis));
            }
        }
    }
}

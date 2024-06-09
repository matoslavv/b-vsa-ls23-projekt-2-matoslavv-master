/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stuba.fei.uim.vsa.pr2.dto;

import sk.stuba.fei.uim.vsa.pr1.solution.Student;
import sk.stuba.fei.uim.vsa.pr1.solution.Thesis;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 *
 * @author edu
 */
public class StudentDTO {
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
    public ThesisDTO thesis; // check atributy
    
    public StudentDTO(Student s) {
        this.id = s.getAisId();
        this.aisId = s.getAisId();
        this.email = s.getEmail();

        if (s.getName() != null) {
            this.name = s.getName();
        }

        if (s.getYear() != null) {
            this.year = s.getYear();
        }

        if (s.getTerm() != null) {
            this.term = s.getTerm();
        }

        if (s.getProgramme() != null) {
            this.programme = s.getProgramme();
        }

        if (s.getThesis() != null) {
            this.thesis = new ThesisDTO(s.getThesis());
        }
    }
}

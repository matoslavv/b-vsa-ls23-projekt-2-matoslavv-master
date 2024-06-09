/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stuba.fei.uim.vsa.pr2.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import sk.stuba.fei.uim.vsa.pr1.solution.Thesis;
import sk.stuba.fei.uim.vsa.pr1.solution.ThesisStatus;
import sk.stuba.fei.uim.vsa.pr1.solution.ThesisType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 *
 * @author edu
 */
public class ThesisDTO {
    @NotEmpty
    public Long id;
    @Pattern(regexp = "^FEI-\\w+$")
    @NotEmpty
    public String registrationNumber;
    @NotEmpty
    public String title;
    public String description;
    @NotEmpty
    public String department;
    @NotEmpty
    public TeacherAltResponse supervisor;
    public StudentAltResponse author;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date publishedOn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date deadline;
    @NotEmpty
    public ThesisType type;
    public ThesisStatus status;
    
    public ThesisDTO(Thesis t) {
        this.id = t.getId();
        this.registrationNumber = t.getRegistrationNumber();
        this.title = t.getTitle();
        this.department = t.getDepartment();
        this.supervisor = new TeacherAltResponse(t.getSupervisor());
        this.type = t.getType();

        if (t.getDescription() != null) {
            this.description = t.getDescription();
        }

        if (t.getAuthor() != null) {
            this.author = new StudentAltResponse(t.getAuthor());
        }

        if (t.getPublishedOn() != null) {
            this.publishedOn = t.getPublishedOn();
        }

        if (t.getDeadline() != null) {
            this.deadline = t.getDeadline();
        }

        if (t.getStatus() != null) {
            this.status = t.getStatus();
        }
    }
}

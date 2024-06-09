package sk.stuba.fei.uim.vsa.pr2.dto;

import lombok.Data;
import sk.stuba.fei.uim.vsa.pr1.solution.Thesis;
import sk.stuba.fei.uim.vsa.pr1.solution.ThesisType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class CreateThesisRequest {
    @NotEmpty
    @Pattern(regexp = "^FEI-\\w+$")
    private String registrationNumber;
    @NotEmpty
    private String title;
    private String description;
    @NotEmpty
    private ThesisType type;

    public CreateThesisRequest(Thesis thesis) {
        this.registrationNumber = thesis.getRegistrationNumber();
        this.title = thesis.getTitle();
        this.type = thesis.getType();

        if (thesis.getDescription() != null) {
            this.description = thesis.getDescription();
        }
    }

    public CreateThesisRequest() {
    }
}

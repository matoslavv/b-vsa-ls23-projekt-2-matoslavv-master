package sk.stuba.fei.uim.vsa.pr2.errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import sk.stuba.fei.uim.vsa.pr2.errors.ErrorType;

import javax.validation.constraints.Pattern;

@Data
@Builder
public class Message {
    @Pattern(regexp = "^\\d{3}$")
    private Integer code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ErrorType errorType;

    public Message(Integer code, String message, ErrorType errorType) {
        this.code = code;
        this.message = message;
        this.errorType = errorType;
    }

    public Message(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Message() {
    }
}

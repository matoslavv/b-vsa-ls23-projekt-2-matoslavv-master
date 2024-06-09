package sk.stuba.fei.uim.vsa.pr2.errors;

import lombok.Data;

@Data
public class ErrorType {
    private String type;
    private String trace;

    public ErrorType(String type, String trace) {
        this.type = type;
        this.trace = trace;
    }

    public ErrorType() {
    }
}

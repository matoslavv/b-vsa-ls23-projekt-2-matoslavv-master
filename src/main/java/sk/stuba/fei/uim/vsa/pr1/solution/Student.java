package sk.stuba.fei.uim.vsa.pr1.solution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.security.Principal;

@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name = "STUDENT")
@NamedQuery(name = Student.FIND_ALL_QUERY, query = "select s from Student s")
public class Student extends User implements Serializable {
    private static final long serialVersionUID = -8905656348104328114L;

    public static final String FIND_ALL_QUERY = "Student.findAll";

    private Integer year;
    private Integer term;
    private String programme;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "author")
    private Thesis thesis;

    public Student() {
        super();
    }
}

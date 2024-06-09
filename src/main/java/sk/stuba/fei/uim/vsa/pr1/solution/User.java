package sk.stuba.fei.uim.vsa.pr1.solution;

import lombok.AllArgsConstructor;
import lombok.Data;
import sk.stuba.fei.uim.vsa.pr2.auth.Permission;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@MappedSuperclass
public class User implements Principal {
    @Id
    @Column(unique = true, nullable = false)
    protected Long aisId;

    @Column(unique = true, nullable = false)
    protected Long id;

    @Column(nullable = false)
    protected String name;

    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}")
    @Column(unique = true, nullable = false)
    protected String email;

    protected String password;

    @ElementCollection
    protected List<Permission> permissions;

    public void addPermission(Permission permission) {
        permissions.add(permission);
    }

    public Long getAisId() {
        return aisId;
    }

    public void setAisId(Long aisId) {
        this.aisId = aisId;
        this.id = aisId;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User() {
        this.permissions = new ArrayList<>();
    }
}

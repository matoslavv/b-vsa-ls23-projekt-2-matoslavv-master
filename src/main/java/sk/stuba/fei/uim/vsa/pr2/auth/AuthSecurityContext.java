package sk.stuba.fei.uim.vsa.pr2.auth;

import sk.stuba.fei.uim.vsa.pr1.solution.User;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class AuthSecurityContext implements SecurityContext {

    private final User user;
    private boolean secure;

    public AuthSecurityContext(User user) {
        this.user = user;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    @Override
    public Principal getUserPrincipal() {
        return user;
    }

    @Override
    public boolean isUserInRole(String s) {
        return true;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public String getAuthenticationScheme() {
        return "Basic";
    }
}

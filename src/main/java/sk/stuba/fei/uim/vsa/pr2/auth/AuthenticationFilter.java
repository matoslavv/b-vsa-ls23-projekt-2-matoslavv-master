package sk.stuba.fei.uim.vsa.pr2.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import sk.stuba.fei.uim.vsa.pr1.solution.Student;
import sk.stuba.fei.uim.vsa.pr1.solution.Teacher;
import sk.stuba.fei.uim.vsa.pr1.solution.ThesisService;
import sk.stuba.fei.uim.vsa.pr1.solution.User;
import sk.stuba.fei.uim.vsa.pr2.BCryptService;
import sk.stuba.fei.uim.vsa.pr2.errors.ErrorType;
import sk.stuba.fei.uim.vsa.pr2.errors.Message;
import sk.stuba.fei.uim.vsa.pr2.errors.UnauthorizedException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private final ObjectMapper jsonMapper;
    private final ThesisService thesisService;

    public AuthenticationFilter() {
        this.jsonMapper = new ObjectMapper();
        thesisService = new ThesisService();
    }

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        boolean abort = false;
        String authHeader = request.getHeaderString(HttpHeaders.AUTHORIZATION);

        try {
            if (authHeader == null || !authHeader.contains("Basic")) {
                throw new UnauthorizedException("Unauthorized request");
            }

            String[] userCredentials = getAuthHeaderData(authHeader);
            log.info("Credentials: " + userCredentials[0] + ", " + userCredentials[1]);

            User loggedStudent = thesisService.getUserByEmail(userCredentials[0], Student.class);
            User loggedTeacher = thesisService.getUserByEmail(userCredentials[0], Teacher.class);

//            if (loggedStudent == null || !BCryptService.verify(new String(Base64.getDecoder().decode(userCredentials[1])), loggedStudent.getPassword())) {
            if (loggedStudent == null || !BCryptService.verify(userCredentials[1], loggedStudent.getPassword())) {
                abort = true;
            }

//            if (abort && (loggedTeacher == null || !BCryptService.verify(new String(Base64.getDecoder().decode(userCredentials[1])), loggedTeacher.getPassword()))) {
            if (abort && (loggedTeacher == null || !BCryptService.verify(userCredentials[1], loggedTeacher.getPassword()))) {
                throw new UnauthorizedException("Unauthorized request");
            }

            final SecurityContext securityContext = request.getSecurityContext();
            AuthSecurityContext context = new AuthSecurityContext(loggedStudent == null ? loggedTeacher : loggedStudent);
            context.setSecure(securityContext.isSecure());
            request.setSecurityContext(context);
        } catch (UnauthorizedException e) {
            request.abortWith(Response
                        .status(401)
                        .header(HttpHeaders.WWW_AUTHENTICATE, "Basic VSA")
                        .entity(this.jsonMapper.writeValueAsString(new Message(401, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace())))))
                        .build());
        }
    }

    private String[] getAuthHeaderData(String authHeader) {
        return new String(Base64.getDecoder()
                .decode(authHeader.replace("Basic", "").trim()))
                .split(":");
    }
}

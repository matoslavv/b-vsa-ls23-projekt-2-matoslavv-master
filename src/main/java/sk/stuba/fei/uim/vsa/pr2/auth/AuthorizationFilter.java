package sk.stuba.fei.uim.vsa.pr2.auth;


import lombok.extern.slf4j.Slf4j;
import sk.stuba.fei.uim.vsa.pr1.solution.User;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Secured
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
//        log.error("***********AUTHORIZATION***********");
//        User user = (User) request.getSecurityContext().getUserPrincipal();
//
//        log.error(user.getName());
//        Method resourceMethod = resourceInfo.getResourceMethod();
//        log.error(resourceMethod.getName() + " ... " + resourceMethod.getDeclaringClass());
//
//        for(Parameter p : resourceMethod.getParameters()) {
////            log.error(p.());
//        }
//
//
//        Set<Permission> permissions = extractPermissionsFromMethod(resourceMethod);
//
//        log.error("******************************************METHOD NAME: " + resourceMethod.getName());
    }

    private Set<Permission> extractPermissionsFromMethod(Method method) {
        if (method == null) {
            return new HashSet<>();
        }

        Secured secured = method.getAnnotation(Secured.class);
        if (secured == null) {
            return new HashSet<>();
        }

        return new HashSet<>(Arrays.asList(secured.value()));
    }
}

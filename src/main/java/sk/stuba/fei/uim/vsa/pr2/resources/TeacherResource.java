/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stuba.fei.uim.vsa.pr2.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import sk.stuba.fei.uim.vsa.pr1.solution.Teacher;
import sk.stuba.fei.uim.vsa.pr1.solution.ThesisService;
import sk.stuba.fei.uim.vsa.pr1.solution.User;
import sk.stuba.fei.uim.vsa.pr2.errors.ErrorType;
import sk.stuba.fei.uim.vsa.pr2.errors.Message;
import sk.stuba.fei.uim.vsa.pr2.auth.Secured;
import sk.stuba.fei.uim.vsa.pr2.dto.CreateTeacherRequest;
import sk.stuba.fei.uim.vsa.pr2.dto.TeacherDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

/**
 * REST Web Service
 *
 * @author edu
 */
@Slf4j
@Path("/teachers")
public class TeacherResource {
    private final ThesisService thesisService;
    private final ObjectMapper jsonMapper;

    public TeacherResource() {
        this.thesisService = new ThesisService();
        this.jsonMapper = new ObjectMapper();
    }

    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response index(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws JsonProcessingException {
        try {
            List<Teacher> teachers = this.thesisService.getTeachers();
            List<TeacherDTO> teachersDTO = new ArrayList<>();

            for (Teacher teacher : teachers) {
                teachersDTO.add(new TeacherDTO(teacher));
            }

            return Response.ok(this.jsonMapper.writeValueAsString(teachersDTO)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        }
    }
    
    @GET
    @Secured
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response read(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathParam("id") Long id) throws JsonProcessingException {
        try {
            Teacher teacher = this.thesisService.getTeacher(id);
            if (teacher == null) {
                throw new NotFoundException("Teacher was not found");
            }

            return Response.ok(this.jsonMapper.writeValueAsString(new TeacherDTO(teacher))).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(this.jsonMapper.writeValueAsString(new Message(404, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        }
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader, String body) throws JsonProcessingException {
        try {
            CreateTeacherRequest request = this.jsonMapper.readValue(body, CreateTeacherRequest.class);

            if (request == null) {
                throw new BadRequestException("Required fields are not filled.");
            }

            if (request.getAisId() == null || request.getName() == null || request.getEmail() == null || request.getPassword() == null) {
                throw new BadRequestException("Required fields are not filled.");
            }

//            Teacher teacher = this.thesisService.createTeacher(request.getAisId(), request.getName(), request.getEmail(), Base64.getEncoder().encodeToString(request.getPassword().getBytes()), Optional.ofNullable(request.getDepartment()), Optional.ofNullable(request.getInstitute()));
            Teacher teacher = this.thesisService.createTeacher(request.getAisId(), request.getName(), request.getEmail(), request.getPassword(), Optional.ofNullable(request.getDepartment()), Optional.ofNullable(request.getInstitute()));

            return Response.status(Response.Status.CREATED).entity(this.jsonMapper.writeValueAsString(new TeacherDTO(teacher))).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(this.jsonMapper.writeValueAsString(new Message(400, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        }
    }
    
    @DELETE
    @Secured
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Long id, @Context SecurityContext securityContext) throws JsonProcessingException {
       User loggedUser = (User) securityContext.getUserPrincipal();

       try {
            if (!(loggedUser instanceof Teacher && Objects.equals(loggedUser.getAisId(), id))) {
                throw new ForbiddenException("Authorized user doesn't have permission for the request.");
            }

            Teacher teacher = this.thesisService.getTeacher(id);
            if (teacher == null) {
                throw new NotFoundException("Student was not found");
            }

           Teacher removedTeacher = this.thesisService.deleteTeacher(id);

            return Response.ok(this.jsonMapper.writeValueAsString(new TeacherDTO(removedTeacher))).build();
       } catch (ForbiddenException e) {
           return Response.status(Response.Status.FORBIDDEN).entity(this.jsonMapper.writeValueAsString(new Message(403, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
       } catch (NotFoundException e) {
           return Response.status(Response.Status.NOT_FOUND).entity(this.jsonMapper.writeValueAsString(new Message(404, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
       } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
       }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stuba.fei.uim.vsa.pr2.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import lombok.extern.slf4j.Slf4j;
import sk.stuba.fei.uim.vsa.pr1.solution.Student;
import sk.stuba.fei.uim.vsa.pr1.solution.Teacher;
import sk.stuba.fei.uim.vsa.pr1.solution.ThesisService;
import sk.stuba.fei.uim.vsa.pr1.solution.User;
import sk.stuba.fei.uim.vsa.pr2.errors.ErrorType;
import sk.stuba.fei.uim.vsa.pr2.errors.Message;
import sk.stuba.fei.uim.vsa.pr2.auth.Secured;
import sk.stuba.fei.uim.vsa.pr2.dto.CreateStudentRequest;
import sk.stuba.fei.uim.vsa.pr2.dto.StudentDTO;

/**
 * REST Web Service
 *
 * @author edu
 */
@Slf4j
@Path("/students")
public class StudentResource {
    private final ThesisService thesisService;
    private final ObjectMapper jsonMapper;

    public StudentResource() {
        this.thesisService = new ThesisService();
        this.jsonMapper = new ObjectMapper();
    }

    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response index(@Context SecurityContext securityContext) throws JsonProcessingException {
        try {
            List<Student> students = this.thesisService.getStudents();

            List<StudentDTO> studentsDTO = new ArrayList<>();
            for (Student student : students) {

                if (student.getThesis() != null)
                    log.info("Student's thesis: " + student.getThesis().getId());
                studentsDTO.add(new StudentDTO(student));
            }

            return Response.ok(this.jsonMapper.writeValueAsString(studentsDTO)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        }
    }

    @GET
    @Secured
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response read(@PathParam("id") Long id) throws JsonProcessingException {
        try {
            if (id == null) {
                throw new BadRequestException("Student ID was not attached.");
            }

            Student student = this.thesisService.getStudent(id);
            if (student == null) {
                throw new NotFoundException("Student was not found");
            }

            return Response.ok(this.jsonMapper.writeValueAsString(new StudentDTO(student))).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(this.jsonMapper.writeValueAsString(new Message(400, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(this.jsonMapper.writeValueAsString(new Message(404, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String body) throws JsonProcessingException {
        try {
            CreateStudentRequest request = this.jsonMapper.readValue(body, CreateStudentRequest.class);

            if (request == null) {
                throw new BadRequestException("Required fields are not filled.");
            }

            if (request.getAisId() == null || request.getName() == null || request.getEmail() == null || request.getPassword() == null) {
                throw new BadRequestException("Required fields are not filled.");
            }

//            Student student = this.thesisService.createStudent(request.getAisId(), request.getName(), request.getEmail(), Base64.getEncoder().encodeToString(request.getPassword().getBytes()), Optional.ofNullable(request.getYear()), Optional.ofNullable(request.getTerm()), Optional.ofNullable(request.getProgramme()));
            Student student = this.thesisService.createStudent(request.getAisId(), request.getName(), request.getEmail(), request.getPassword(), Optional.ofNullable(request.getYear()), Optional.ofNullable(request.getTerm()), Optional.ofNullable(request.getProgramme()));

            return Response.status(Response.Status.CREATED).entity(this.jsonMapper.writeValueAsString(new StudentDTO(student))).build();
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
    public Response delete(@PathParam("id") Long id, @Context SecurityContext context) throws JsonProcessingException {
        User loggedUser = (User) context.getUserPrincipal();

        try {
            if (!(loggedUser instanceof Teacher) && !Objects.equals(loggedUser.getAisId(), id)) {
                throw new ForbiddenException("Authorized user doesn't have permission for the request.");
            }

            Student student = this.thesisService.getStudent(id);
            if (student == null) {
                throw new NotFoundException("Student was not found");
            }

            return Response.ok(this.jsonMapper.writeValueAsString(new StudentDTO(this.thesisService.deleteStudent(id)))).build();
        } catch (ForbiddenException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(this.jsonMapper.writeValueAsString(new Message(403, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(this.jsonMapper.writeValueAsString(new Message(404, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        }
    }
}
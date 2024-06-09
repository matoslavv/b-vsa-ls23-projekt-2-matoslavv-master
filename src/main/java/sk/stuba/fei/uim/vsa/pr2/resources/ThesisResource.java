/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stuba.fei.uim.vsa.pr2.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import sk.stuba.fei.uim.vsa.pr1.solution.*;
import sk.stuba.fei.uim.vsa.pr2.errors.ErrorType;
import sk.stuba.fei.uim.vsa.pr2.errors.Message;
import sk.stuba.fei.uim.vsa.pr2.auth.Secured;
import sk.stuba.fei.uim.vsa.pr2.dto.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.*;

/**
 * REST Web Service
 *
 * @author edu
 */
@Slf4j
@Path("/theses")
public class ThesisResource {
    private final ThesisService thesisService;
    private final ObjectMapper jsonMapper;

    public ThesisResource() {
        this.thesisService = new ThesisService();
        this.jsonMapper = new ObjectMapper();
    }

    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response index() throws JsonProcessingException {
        try {
            List<Thesis> theses = this.thesisService.getTheses();
            List<ThesisDTO> thesesDTO = new ArrayList<>();

            for(Thesis thesis : theses) {
                thesesDTO.add(new ThesisDTO(thesis));
            }

            return Response.ok(this.jsonMapper.writeValueAsString(thesesDTO)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e.getMessage(), new ErrorType()))).build();
        }
    }
    
    @GET
    @Secured
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response read(@PathParam("id") Long id) throws JsonProcessingException {
        try {
            Thesis thesis = this.thesisService.getThesis(id);
            if (thesis == null) {
                throw new NotFoundException("Thesis was not found");
            }

            return Response.ok(this.jsonMapper.writeValueAsString(new ThesisDTO(thesis))).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(this.jsonMapper.writeValueAsString(new Message(404, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        }
    }
    
    @POST
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String body, @Context SecurityContext securityContext) throws JsonProcessingException {
        User loggedUser = (User) securityContext.getUserPrincipal();

        try {
            if (!(loggedUser instanceof Teacher)) {
                throw new ForbiddenException("Authorized user doesn't have permission for the request.");
            }

            CreateThesisRequest request = this.jsonMapper.readValue(body, CreateThesisRequest.class);
            if (request == null) {
                throw new BadRequestException("Required fields are not filled.");
            }

            if (request.getRegistrationNumber() == null || request.getTitle() == null || request.getType() == null) {
                throw new BadRequestException("Required fields are not filled.");
            }

            Thesis thesis = this.thesisService.makeThesisAssignment(loggedUser.getAisId(), request.getTitle(), request.getType(), request.getRegistrationNumber(), Optional.ofNullable(request.getDescription()));

            return Response.status(Response.Status.CREATED).entity(this.jsonMapper.writeValueAsString(new ThesisDTO(thesis))).build();
        } catch (ForbiddenException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(this.jsonMapper.writeValueAsString(new Message(403, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(this.jsonMapper.writeValueAsString(new Message(400, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        }
    }
    
    @Path("/{id}")
    @DELETE
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Long id, @Context SecurityContext securityContext) throws JsonProcessingException {
        User loggedUser = (User) securityContext.getUserPrincipal();

        try {
            if (!(loggedUser instanceof Teacher)) {
                throw new ForbiddenException("Authorized user doesn't have permission for the request.");
            }

            Thesis thesis = this.thesisService.getThesis(id);
            if (thesis == null) {
                throw new NotFoundException("Thesis was not found");
            }

            if (!Objects.equals(thesis.getSupervisor().getId(), loggedUser.getAisId())) {
                throw new ForbiddenException("Authorized user doesn't have permission for the request.");
            }

            Thesis removedThesis = this.thesisService.deleteThesis(id);

            return Response.ok(this.jsonMapper.writeValueAsString(new ThesisDTO(removedThesis))).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(this.jsonMapper.writeValueAsString(new Message(404, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        } catch (ForbiddenException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(this.jsonMapper.writeValueAsString(new Message(403, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        }
    }

    @Path("/{id}/assign")
    @POST
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response assign(String body, @PathParam("id") Long id, @Context SecurityContext securityContext) throws JsonProcessingException {
        User loggedUser = (User) securityContext.getUserPrincipal();
        Long studentId = null;

        try {
            if (loggedUser instanceof Student) {
                studentId = loggedUser.getAisId();
            }

            if (loggedUser instanceof Teacher) {
                StudentIdRequest request = this.jsonMapper.readValue(body, StudentIdRequest.class);
                studentId = request.getStudentId();
            }

            Thesis thesis = this.thesisService.getThesis(id);
            if (thesis == null) {
                throw new NotFoundException("Thesis was not found");
            }

            Thesis thesisAssigned = this.thesisService.assignThesis(id, studentId);

            return Response.ok(this.jsonMapper.writeValueAsString(new ThesisDTO(thesisAssigned))).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(this.jsonMapper.writeValueAsString(new Message(404, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        }
    }

    @Path("/{id}/submit")
    @POST
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response submit(String body, @PathParam("id") Long id, @Context SecurityContext securityContext) throws JsonProcessingException {
        User loggedUser = (User) securityContext.getUserPrincipal();
        Long studentId = null;

        try {
            Thesis thesis = this.thesisService.getThesis(id);
            if (thesis == null) {
                throw new NotFoundException("Thesis was not found");
            }

            if (thesis.getAuthor() == null) {
                throw new BadRequestException("Thesis doesn't have any author.");
            }

            if (loggedUser instanceof Student && ((Objects.equals(thesis.getAuthor().getAisId(), loggedUser.getAisId())))) {
                studentId = loggedUser.getAisId();
            }

            if (loggedUser instanceof Teacher) {
                StudentIdRequest request = this.jsonMapper.readValue(body, StudentIdRequest.class);
                studentId = request.getStudentId();

                if (studentId == null) {
                    throw new BadRequestException("Student ID was not correctly specified.");
                }

                if (!Objects.equals(thesis.getAuthor().getAisId(), request.getStudentId())) {
                    throw new ForbiddenException("Student is not the author of specified thesis.");
                }
            }

            if (studentId == null) {
                throw new ForbiddenException("Authorized user doesn't have permission for the request.");
            }

            Thesis submittedThesis = this.thesisService.submitThesis(id);

            return Response.ok(this.jsonMapper.writeValueAsString(new ThesisDTO(submittedThesis))).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(this.jsonMapper.writeValueAsString(new Message(404, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        } catch (ForbiddenException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(this.jsonMapper.writeValueAsString(new Message(403, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(this.jsonMapper.writeValueAsString(new Message(400, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        }
    }
}

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
import sk.stuba.fei.uim.vsa.pr2.dto.ThesisDTO;
import sk.stuba.fei.uim.vsa.pr2.dto.ThesisSearchRequest;

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
@Path("/search")
public class SearchResource {
    private final ThesisService thesisService;
    private final ObjectMapper jsonMapper;

    public SearchResource() {
        this.thesisService = new ThesisService();
        this.jsonMapper = new ObjectMapper();
    }

    @Path("/theses")
    @POST
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(String body, @Context SecurityContext securityContext) throws JsonProcessingException {
        try {
            ThesisSearchRequest request = this.jsonMapper.readValue(body, ThesisSearchRequest.class);

            if (request.getStudentId() != null && request.getTeacherId() != null) {
                throw new BadRequestException("Fill in Student ID or Teacher ID only");
            }

            List<Thesis> theses = this.thesisService.searchThesis(request.getStudentId(), request.getTeacherId());
            List<ThesisDTO> thesesDTO = new ArrayList<>();
            for(Thesis thesis : theses) {
                thesesDTO.add(new ThesisDTO(thesis));
            }

            return Response.ok(this.jsonMapper.writeValueAsString(thesesDTO)).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(this.jsonMapper.writeValueAsString(new Message(400, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(this.jsonMapper.writeValueAsString(new Message(500, e.getMessage(), new ErrorType(e.getClass().getName(), Arrays.toString(e.getStackTrace()))))).build();
        }
    }
}
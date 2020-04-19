package org.apache.ofbiz.jersey.resource;


import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.jersey.contact.ContactDTO;
import org.apache.ofbiz.jersey.contact.ContactService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.List;


@Path("/contacts")
@Provider
public class ContactResourse {

    public static final String MODULE = ContactResourse.class.getName();


    @Context
    private HttpServletRequest httpRequest;

    @Context
    private ServletContext servletContext;
    private ContactService contactService;



    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getContacts() throws GenericEntityException {

        List<ContactDTO> entity = contactService.getContacts();

        System.out.println(entity);
        Response.ResponseBuilder builder = Response.
                status(Response.Status.OK)
                .type(MediaType.APPLICATION_JSON)
                .entity(entity);
        return builder.build();
    }


}

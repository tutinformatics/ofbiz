package org.apache.ofbiz.jersey.resource;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.project.model.ProjectCmd;
import org.apache.ofbiz.project.model.ProjectTaskCmd;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.Collections;
import java.util.Map;

import static javax.ws.rs.core.Response.Status;

@Provider
@Path("project")
public class ProjectResource {

    public static final String module = ProjectResource.class.getName();

    private static final ObjectMapper mapper = new ObjectMapper();

    @Context
    private HttpServletRequest httpRequest;

    @Context
    private ServletContext servletContext;

    @POST
    @Path("new-task")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTask(ProjectTaskCmd projectTaskCmd) {
        LocalDispatcher dispatcher = (LocalDispatcher) servletContext.getAttribute("dispatcher");
        Map<String, Object> result;

        try {
            Map<String, Object> context = mapper.convertValue(projectTaskCmd, Map.class);
            result = dispatcher.runSync("createProjectTask", context);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Exception thrown while running createProjectTask service: ",
                    module);
            String errMsg = UtilProperties.getMessage("JerseyUiLabels", "api.error.create_task", httpRequest.getLocale());
            throw new RuntimeException(errMsg);
        }

        if (ServiceUtil.isError(result)) {
            ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
        }

        return Response
                .status(Status.CREATED)
                .type(MediaType.APPLICATION_JSON)
                .entity(result)
                .build();
    }

    @GET
    @Path("task-list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectTaskList(@QueryParam(value = "projectId") String projectId) {
        LocalDispatcher dispatcher = (LocalDispatcher) servletContext.getAttribute("dispatcher");
        Map<String, Object> result;

        try {
            result = dispatcher.runSync("getProjectTaskList", Collections.singletonMap("projectId", projectId));
        } catch (GenericServiceException e) {
            Debug.logError(e, "Exception thrown while running getProjectTaskList service: ",
                    module);
            String errMsg = UtilProperties.getMessage("JerseyUiLabels", "api.error.get_task_list", httpRequest.getLocale());
            throw new RuntimeException(errMsg);
        }

        if (ServiceUtil.isError(result)) {
            ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
        }

        return Response
                .status(Status.OK)
                .type(MediaType.APPLICATION_JSON)
                .entity(result)
                .build();
    }

    @GET
    @Path("project-list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectList() {
        LocalDispatcher dispatcher = (LocalDispatcher) servletContext.getAttribute("dispatcher");
        Map<String, Object> result;

        try {
            result = dispatcher.runSync("getAllProjectList", Collections.emptyMap());
        } catch (GenericServiceException e) {
            Debug.logError(e, "Exception thrown while running getAllProjectList service: ", module);
            String errMsg = UtilProperties.getMessage("JerseyUiLabels", "api.error.get_project_list", httpRequest.getLocale());
            throw new RuntimeException(errMsg);
        }

        return Response
                .status(Status.OK)
                .type(MediaType.APPLICATION_JSON)
                .entity(result)
                .build();
    }

    @POST
    @Path("create-project")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProject(ProjectCmd projectCmd) {
        LocalDispatcher dispatcher = (LocalDispatcher) servletContext.getAttribute("dispatcher");
        Map<String, Object> result;

        try {
            Map<String, Object> context = mapper.convertValue(projectCmd, Map.class);
            result = dispatcher.runSync("createProject", context);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Exception thrown while running createProject service: ", module);
            String errMsg = UtilProperties.getMessage("JerseyUiLabels", "api.error.create_project", httpRequest.getLocale());
            throw new RuntimeException(errMsg);
        }

        if (ServiceUtil.isError(result)) {
            ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
        }

        return Response
                .status(Status.OK)
                .type(MediaType.APPLICATION_JSON)
                .entity(result)
                .build();

    }
}

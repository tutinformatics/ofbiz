package org.apache.ofbiz.jersey.resource;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.Collections;
import java.util.Map;

import static javax.ws.rs.core.Response.*;

@Provider
@Path("task-list")
public class ProjectResource {

    public static final String MODULE = ProjectResource.class.getName();

    @Context
    private HttpServletRequest httpRequest;

    @Context
    private ServletContext servletContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectTaskList(@QueryParam(value = "projectId") String projectId) {
        LocalDispatcher dispatcher = (LocalDispatcher) servletContext.getAttribute("dispatcher");
        Map<String, Object> result;

        try {
            result = dispatcher.runSync("getProjectTaskList", Collections.singletonMap("projectId", projectId));
        } catch (GenericServiceException e) {
            Debug.logError(e, "Exception thrown while running getProjectTaskList service: ", MODULE);
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

}

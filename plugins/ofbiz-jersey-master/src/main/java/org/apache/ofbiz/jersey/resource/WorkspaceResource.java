package org.apache.ofbiz.jersey.resource;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.taltech.workspace.model.WorkspaceCmd;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.Map;

@Provider
@Path("v1/workspaces")
public class WorkspaceResource {

    public static final String module = WorkspaceResource.class.getName();

    private static final ObjectMapper mapper = new ObjectMapper();

    @Context
    private HttpServletRequest httpRequest;

    @Context
    private ServletContext servletContext;

    @POST
    @Path("new-workspace")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public Response createWorkspace(WorkspaceCmd workspaceCmd) {
        LocalDispatcher dispatcher = (LocalDispatcher) servletContext.getAttribute("dispatcher");
        Map<String, Object> result;

        try {
            Map<String, Object> context = mapper.convertValue(workspaceCmd, Map.class);
            result = dispatcher.runSync("createWorkspaceByJavaService", context);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Exception thrown while running createWorkspace service: ",
                    module);
            String errMsg = UtilProperties.getMessage("JerseyUiLabels", "api.error.create_workspace", httpRequest.getLocale());
            throw new RuntimeException(errMsg);
        }

        if (ServiceUtil.isError(result)) {
            ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
        }

        return Response
                .status(Response.Status.CREATED)
                .type(MediaType.APPLICATION_JSON)
                .entity(result)
                .build();
    }

}

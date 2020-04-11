package org.apache.ofbiz.jersey.resource;



import static javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.entity.GenericDelegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityConditionList;
import org.apache.ofbiz.entity.condition.EntityExpr;
import org.apache.ofbiz.entity.condition.EntityJoinOperator;
import org.apache.ofbiz.entity.condition.EntityOperator;
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
import java.util.*;

@Provider
@Path("project")
public class ProjectResource {

    public static final String MODULE = ProjectResource.class.getName();

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
            Debug.logError(e, "Exception thrown while running createProjectTask service: ", MODULE);
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

    @GET
    @Path("project-list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectList() {

        GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        LocalDispatcher dispatcher = (LocalDispatcher) servletContext.getAttribute("dispatcher");

        EntityConditionList<EntityExpr> condition =
                EntityCondition.makeCondition(
                        Arrays.asList(
                                EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, "PROJECT"),
                                EntityCondition.makeCondition("currentStatusId", EntityOperator.NOT_EQUAL, "PRJ_CLOSED")),
                        EntityJoinOperator.AND);
        List<GenericValue> projects = null;
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            projects = delegator.findList("WorkEffort", condition, Collections.singleton("workEffortId"), Collections.emptyList(), null, false);
            projects.forEach(project -> {
                try {
                    Map<String, Object> subResult = dispatcher.runSync("getProject", Collections.singletonMap("projectId", project.get("workEffortId")));
                    result.add((Map<String, Object>) subResult.get("projectInfo"));
                } catch (GenericServiceException e) {
                    e.printStackTrace();

                }
            });

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        return Response
                .status(Status.OK)
                .type(MediaType.APPLICATION_JSON)
                .entity(result)
                .build();
    }

}

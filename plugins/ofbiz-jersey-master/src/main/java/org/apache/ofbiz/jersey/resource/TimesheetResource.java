package org.apache.ofbiz.jersey.resource;

import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.timesheet.model.TimesheetCmd;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.ServiceUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.Collections;
import java.util.Map;

public class TimesheetResource {
    public static final String module = TimesheetResource.class.getName();

    private static final ObjectMapper mapper = new ObjectMapper();

    @Context
    private HttpServletRequest httpRequest;

    @Context
    private ServletContext servletContext;

    @POST
    @Path("new-timesheet")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public Response createTimesheet(TimesheetCmd timesheetCmd) {
        LocalDispatcher dispatcher = (LocalDispatcher) servletContext.getAttribute("dispatcher");
        Map<String, Object> result;

        try {
            Map<String, Object> context = mapper.convertValue(timesheetCmd, Map.class);
            result = dispatcher.runSync("createTimesheetByJavaService", context);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Exception thrown while running createTimesheet service: ",
                    module);
            String errMsg = UtilProperties.getMessage("JerseyUiLabels", "api.error.create_timesheet", httpRequest.getLocale());
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

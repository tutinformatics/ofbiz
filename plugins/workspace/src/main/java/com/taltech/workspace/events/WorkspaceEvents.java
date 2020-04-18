package com.taltech.workspace.events;

import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WorkspaceEvents {

    public static final String module = WorkspaceEvents.class.getName();

    public static String createWorkspaceEvent(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

        String workspaceGroupId = request.getParameter("workspaceGroupId");
        String name = request.getParameter("name");
        String url = request.getParameter("url");
        String userId = request.getParameter("userId");

        if (UtilValidate.isEmpty(name) || UtilValidate.isEmpty(url)) {
            String errMsg = "Name and URL are required fields on the form and can't be empty.";
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        try {
            dispatcher.runSync("createWorkspaceByJavaService", UtilMisc.toMap("workspaceGroupId", workspaceGroupId,
                    "name",  name, "url", url, "userId", userId));
        } catch (GenericServiceException e) {
            String errMsg = "Unable to create new records in Workspace entity: " + e.toString();
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
        request.setAttribute("_EVENT_MESSAGE_", "Workspace created successfully.");
        return "success";
    }
}

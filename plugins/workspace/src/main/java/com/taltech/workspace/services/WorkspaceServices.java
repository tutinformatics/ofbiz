package com.taltech.workspace.services;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.Map;

public class WorkspaceServices {

    public static final String module = WorkspaceServices.class.getName();

    public static Map<String, Object> createWorkspace(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();

        try {
            GenericValue workspace = delegator.makeValue("Workspace");
            workspace.setNextSeqId();
            workspace.setNonPKFields(context);
            workspace = delegator.create(workspace);
            result.put("workspaceId", workspace.getString("workspaceId"));
            Debug.log("Workspace record created successfully with workspaceId:" + workspace.getString("workspaceId"));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Error in creating record in Workspace entity ......." + "." + module);
        }
        return result;
    }
}

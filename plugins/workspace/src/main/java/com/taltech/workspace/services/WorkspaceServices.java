package com.taltech.workspace.services;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityConditionList;
import org.apache.ofbiz.entity.condition.EntityExpr;
import org.apache.ofbiz.entity.condition.EntityJoinOperator;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.apache.ofbiz.entity.condition.EntityCondition.makeCondition;

public class WorkspaceServices {

    public static final String module = WorkspaceServices.class.getName();

    public static Map<String, Object> createWorkspace(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        List<String> errorMessages = new ArrayList<>();

        try {
            validate(context, delegator, errorMessages);
            if (!errorMessages.isEmpty()) {
                return ServiceUtil.returnError(errorMessages);
            }

            GenericValue workspace = delegator.makeValue("Workspace");
            workspace.setNextSeqId();
            workspace.setNonPKFields(context);
            workspace = delegator.create(workspace);
            result.put("workspaceId", workspace.getString("workspaceId"));
            Debug.log("Workspace record created successfully with workspaceId:" + workspace.getString("workspaceId"));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Exception thrown while running createWorkspace service: " + module);
        }
        return result;
    }

    private static void validate(Map<String, ?> context, Delegator delegator,
                                    List<String> errorMessages) throws GenericEntityException {
        long count = delegator.findCountByCondition("Workspace", buildWorkEffortQuery(context), null, null);
        if (count > 0) {
            errorMessages.add(String.format("Workspace with title %s already exists", context.get("title")));
        }
    }

    private static EntityConditionList<EntityExpr> buildWorkEffortQuery(Map<String, ?> context) {
        return makeCondition(Arrays.asList(
                makeCondition("url", EntityOperator.EQUALS, context.get("url")),
                makeCondition("userId", EntityOperator.EQUALS, context.get("userId")),
                makeCondition("title", EntityOperator.EQUALS, context.get("title"))),
            EntityJoinOperator.AND);
    }
}

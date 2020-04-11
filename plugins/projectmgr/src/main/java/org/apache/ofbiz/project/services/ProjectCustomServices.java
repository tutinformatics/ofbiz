package org.apache.ofbiz.project.services;

import org.apache.commons.collections.MapUtils;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityConditionList;
import org.apache.ofbiz.entity.condition.EntityExpr;
import org.apache.ofbiz.entity.condition.EntityJoinOperator;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.apache.ofbiz.entity.condition.EntityCondition.makeCondition;

public class ProjectCustomServices {

    public static final String module = ProjectCustomServices.class.getName();

    public static Map<String, Object> getProjectList(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        List<Map> projectList = new ArrayList<>();
        List<GenericValue> projects;

        try {
            projects = delegator.findList("WorkEffort", buildProjectListCondition(), Collections.singleton("workEffortId"), Collections.emptyList(), null, false);
            for (GenericValue project : projects) {
                Map<String, Object> subResult = dispatcher.runSync("getProject", Collections.singletonMap("projectId", project.get("workEffortId")));
                projectList.add(MapUtils.getMap(subResult, "projectInfo"));
            }
            result.put("projectList", projectList);
        } catch (GenericEntityException | GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Exception thrown while running getProjectList service: " + module);
        }
        return result;
    }

    private static EntityConditionList<EntityExpr> buildProjectListCondition() {
        return makeCondition(getProjectListConditionList(), EntityJoinOperator.AND);
    }

    private static List<EntityExpr> getProjectListConditionList() {
        return Arrays.asList(
                makeCondition("workEffortTypeId", EntityOperator.EQUALS, "PROJECT"),
                makeCondition("currentStatusId", EntityOperator.NOT_EQUAL, "PRJ_CLOSED"));
    }

}

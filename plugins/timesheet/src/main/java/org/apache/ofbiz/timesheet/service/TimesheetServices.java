package org.apache.ofbiz.timesheet.service;

import com.taltech.workspace.services.WorkspaceServices;
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

import java.util.*;

import static org.apache.ofbiz.entity.condition.EntityCondition.makeCondition;

public class TimesheetServices {
    public static final String module = TimesheetServices.class.getName();

    public static Map<String, Object> createTimesheet(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();

        try {
            GenericValue timesheet = delegator.makeValue("Timesheet");
            timesheet.setNextSeqId();
            timesheet.setNonPKFields(context);
            timesheet = delegator.create(timesheet);
            result.put("timesheetId", timesheet.getString("timesheetId"));
            Debug.log("Timesheet record created successfully with timesheetId:" + timesheet.getString("s"));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Exception thrown while running createTimesheet service: " + module);
        }
        return result;
    }
}

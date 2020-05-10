package org.apache.ofbiz.graphql.utils;

import graphql.schema.DataFetchingEnvironment;
import graphql.servlet.context.DefaultGraphQLServletContext;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.service.LocalDispatcher;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class GetDelegatorAndDispatcher {
    private DataFetchingEnvironment dataFetchingEnvironment;
    private Delegator delegator;
    private LocalDispatcher dispatcher;

    public GetDelegatorAndDispatcher(DataFetchingEnvironment dataFetchingEnvironment) {
        this.dataFetchingEnvironment = dataFetchingEnvironment;
    }

    public Delegator getDelegator() {
        return delegator;
    }

    public LocalDispatcher getDispatcher() {
        return dispatcher;
    }

    public GetDelegatorAndDispatcher invoke() {
        DefaultGraphQLServletContext context = dataFetchingEnvironment.getContext();
        HttpServletRequest request = context.getHttpServletRequest();
        delegator = (Delegator) request.getAttribute("delegator");
        dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

        if (UtilValidate.isEmpty(delegator)) {
            ServletContext servContext = request.getServletContext();
            delegator = (Delegator) servContext.getAttribute("delegator");
        }

        if (UtilValidate.isEmpty(dispatcher)) {
            ServletContext servContext = request.getServletContext();
            dispatcher = (LocalDispatcher) servContext.getAttribute("dispatcher");
        }
        return this;
    }
}

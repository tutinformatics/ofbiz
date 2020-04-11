package org.apache.ofbiz.graphql.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.servlet.context.DefaultGraphQLServletContext;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.util.EntityQuery;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class EntityDataFetcher implements DataFetcher<Object> {

    @Override
    public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws GenericEntityException {

        DefaultGraphQLServletContext context = dataFetchingEnvironment.getContext();
        HttpServletRequest request = context.getHttpServletRequest();
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        if (UtilValidate.isEmpty(delegator)) {
            ServletContext servContext = request.getServletContext();
            delegator = (Delegator) servContext.getAttribute("delegator");
        }

        String entity = dataFetchingEnvironment.getFieldType().getName();

        return EntityQuery.use(delegator).from(entity)
                .where(dataFetchingEnvironment.getArguments())
                .cache()
                .queryOne();

    }
}

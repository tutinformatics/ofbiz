package org.apache.ofbiz.graphql.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.servlet.context.DefaultGraphQLServletContext;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.model.ModelField;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.graphql.utils.QueryParamStringConverter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

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

        GenericValue genericValue = EntityQuery.use(delegator).from(entity)
                .where(dataFetchingEnvironment.getArguments())
                .cache()
                .queryOne();

        Map<String, Object> secondary = new HashMap<>();

        for (String key : genericValue.getAllKeys()) {
            ModelField field = genericValue.getModelEntity().getField(key);
            if (genericValue.get(key) == null) {
                secondary.put(key, null);
            } else {
                secondary.put(key, QueryParamStringConverter.convert(genericValue.get(key).toString(), field.getType()));
            }
        }

        return secondary;

    }
}

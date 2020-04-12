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
import org.jetbrains.annotations.NotNull;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        String entity;

        if (dataFetchingEnvironment.getArguments().size() != 0) { // Get by primary keys
            entity = dataFetchingEnvironment.getFieldType().getName();

            GenericValue genericValue = EntityQuery.use(delegator).from(entity)
                    .where(dataFetchingEnvironment.getArguments())
                    .cache()
                    .queryOne();

            return getStringObjectMap(genericValue);

        } else { // Get all from table
            entity = dataFetchingEnvironment.getFieldType().getChildren().get(0).getName();

            List<GenericValue> genericValue = EntityQuery.use(delegator).from(entity)
                    .cache()
                    .queryList();

            return genericValue.stream().map(this::getStringObjectMap).collect(Collectors.toList());
        }
    }

    @NotNull
    private Map<String, Object> getStringObjectMap(GenericValue genericValue) {
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

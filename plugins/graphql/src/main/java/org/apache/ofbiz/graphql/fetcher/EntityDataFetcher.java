package org.apache.ofbiz.graphql.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.servlet.context.DefaultGraphQLServletContext;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.model.ModelEntity;
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

    /**
     * @Author: Enrico Vompa
     *
     * GET - gets row from database
     * POST - Puts a new row into the database
     * POST_ - Puts a new row into the database while generating a PK for it automatically
     * PUT - Updates an existing row in database
     * DELETE - Removes an existing row from database
     *
     * @return - returns the value specified in arguments
     * **/
    @Override
    public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws GenericEntityException {

        DefaultGraphQLServletContext context = dataFetchingEnvironment.getContext();
        HttpServletRequest request = context.getHttpServletRequest();
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        if (UtilValidate.isEmpty(delegator)) {
            ServletContext servContext = request.getServletContext();
            delegator = (Delegator) servContext.getAttribute("delegator");
        }

        String operation = dataFetchingEnvironment.getField().getName();

        if (operation.startsWith("delete")) { // DELETE
            String entity = dataFetchingEnvironment.getFieldType().getName();
            GenericValue genericValue = EntityQuery.use(delegator).from(entity)
                    .where(dataFetchingEnvironment.getArguments())
                    .cache()
                    .queryOne();
            delegator.removeValue(GenericValue.create(genericValue));
            return getStringObjectMap(genericValue);

        } else if (operation.startsWith("post")) { // POST
            if (operation.endsWith("_")) { // auto generate pk
                String entity = dataFetchingEnvironment.getFieldType().getName();
                Map<String, Object> argum = dataFetchingEnvironment.getArguments().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                ModelEntity modelEntity = delegator.getModelEntity(entity);
                List<String> pks = modelEntity.getPkFieldNames();
                GenericValue value = new GenericValue().create(delegator, modelEntity, argum);
                value.setNextSeqId();
                delegator.create(value);
                argum.put(pks.get(0), value.get(pks.get(0)));
                return argum;

            } else {
                String entity = dataFetchingEnvironment.getFieldType().getName();
                ModelEntity modelEntity = delegator.getModelEntity(entity);
                GenericValue value = new GenericValue().create(delegator, modelEntity, dataFetchingEnvironment.getArguments());
                delegator.create(value);
                return dataFetchingEnvironment.getArguments();
            }


        } else if (operation.startsWith("put")) { // PUT
            String entity = dataFetchingEnvironment.getFieldType().getName();
            ModelEntity modelEntity = delegator.getModelEntity(entity);
            GenericValue genericValue = GenericValue.create(delegator, modelEntity, dataFetchingEnvironment.getArguments());
            delegator.store(genericValue);
            return dataFetchingEnvironment.getArguments();


        } else { // GET
            if (dataFetchingEnvironment.getArguments().size() != 0) { // Get by primary keys
                String entity = dataFetchingEnvironment.getFieldType().getName();

                GenericValue genericValue = EntityQuery.use(delegator).from(entity)
                        .where(dataFetchingEnvironment.getArguments())
                        .cache()
                        .queryOne();

                return getStringObjectMap(genericValue);

            } else { // Get all from table
                String entity = dataFetchingEnvironment.getFieldType().getChildren().get(0).getName();

                List<GenericValue> genericValue = EntityQuery.use(delegator).from(entity)
                        .cache()
                        .queryList();

                return genericValue.stream().map(this::getStringObjectMap).collect(Collectors.toList());
            }
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

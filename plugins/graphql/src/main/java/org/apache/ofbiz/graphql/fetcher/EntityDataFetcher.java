package org.apache.ofbiz.graphql.fetcher;

import graphql.language.Field;
import graphql.language.Node;
import graphql.language.Selection;
import graphql.language.SelectionSet;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.model.ModelEntity;
import org.apache.ofbiz.entity.model.ModelField;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.graphql.utils.GetDelegatorAndDispatcher;
import org.apache.ofbiz.graphql.utils.QueryParamStringConverter;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityDataFetcher implements DataFetcher<Object> {

    /**
     * @return - returns the value specified in arguments
     *
     * <p>
     * GET - gets row from database
     * POST - Puts a new row into the database
     * POST_ - Puts a new row into the database while generating a PK for it automatically
     * PUT - Updates an existing row in database
     * DELETE - Removes an existing row from database
     **/
    @Override
    public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws GenericEntityException, GenericServiceException {

        GetDelegatorAndDispatcher getDelegatorAndDispatcher = new GetDelegatorAndDispatcher(dataFetchingEnvironment).invoke();
        Delegator delegator = getDelegatorAndDispatcher.getDelegator();
        LocalDispatcher dispatcher = getDelegatorAndDispatcher.getDispatcher();

        String operation = dataFetchingEnvironment.getField().getName();

        if (operation.equals("services_")) { // GET services
            return dispatcher.getDispatchContext().getAllServiceNames();

        } /* else if (operation.startsWith("service_")) {

            String serviceName = operation.replace("service_", "");
            for (ModelParam param : dispatcher.getDispatchContext().getModelService(operation.replace("service_", "")).getModelParamList()) {
            }
            return dispatcher.getDispatchContext().getModelService(operation.replace("service_", "")).getModelParamList();

        } */ else if (operation.startsWith("delete")) { // DELETE
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
                GenericValue value = GenericValue.create(delegator, modelEntity, argum);
                value.setNextSeqId();
                delegator.create(value);
                argum.put(pks.get(0), value.get(pks.get(0)));
                return argum;

            } else {
                String entity = dataFetchingEnvironment.getFieldType().getName();
                ModelEntity modelEntity = delegator.getModelEntity(entity);
                GenericValue value = GenericValue.create(delegator, modelEntity, dataFetchingEnvironment.getArguments());
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

                Map<String, Object> returnable = new HashMap<>();
                fillSubfieldsRecursively(returnable, genericValue, (SelectionSet) dataFetchingEnvironment.getDocument().getDefinitions().get(0).getChildren().get(0));

                return returnable;

            } else { // Get all from table
                String entity = dataFetchingEnvironment.getFieldType().getChildren().get(0).getName();

                List<GenericValue> genericValue = EntityQuery.use(delegator).from(entity)
                        .cache()
                        .queryList();

                List<Map<String, Object>> returnable = new ArrayList<>();

                for (int i = 0; i < genericValue.size(); i++) {
                    returnable.add(new HashMap<>());
                    for (Object va : dataFetchingEnvironment.getDocument().getDefinitions().get(0).getChildren()) {
                        fillSubfieldsRecursively(returnable.get(i), genericValue.get(i), (SelectionSet) va);
                    }
                }

                return returnable;
            }
        }
    }

    /**
     * Wrapper for graphQL datatype that makes recursive calls happen.
     **/
    private void fillSubfieldsRecursively(Map<String, Object> returnable, GenericValue genericValue, SelectionSet fields) {
        if (fields == null) {
            return;
        }

        for (Selection selection : fields.getSelections()) {
            getStringObjectMap(genericValue).forEach(returnable::put);

            try {
                goDeeper(returnable, genericValue, (Field) selection);
            } catch (Exception e) {
            }

            try {
                for (Object a : selection.getChildren()) {
                    SelectionSet se = (SelectionSet) a;
                    for (Object selection_ : se.getChildren()) {
                        goDeeper(returnable, genericValue, (Field) selection_);
                    }
                }
            } catch (Exception e) {
            }

        }
    }

    /**
     * Fills toOne and toMany subqueries recursively
     *
     * @param returnable:   returable map - edited inplace.
     * @param genericValue: genericValue used to fill the fields.
     * @param field:        graphQL field from where recursive calls are called from
     **/
    private void goDeeper(Map<String, Object> returnable, GenericValue genericValue, Field field) {

        if (field.getName().startsWith("_toOne_")) {
            try {
                GenericValue child = genericValue.getRelatedOne(field.getName().replace("_toOne_", ""), true);
                Map<String, Object> returnable_ = new HashMap<>();
                for (Node child_ : field.getChildren()) {
                    fillSubfieldsRecursively(returnable_, child, (SelectionSet) child_);
                }
                returnable.put(field.getName(), returnable_);

            } catch (Exception e) {
                returnable.put(field.getName(), null);
            }
        }

        if (field.getName().startsWith("_toMany_")) {
            try {

                List<GenericValue> children = genericValue.getRelated(field.getName().replace("_toMany_", ""));
                returnable.put(field.getName(), children);
                List<Map<String, Object>> returnable_ = new ArrayList<>();

                for (int i = 0; i < children.size(); i++) {
                    returnable_.add(new HashMap<>());
                    for (Node child_ : field.getChildren()) {
                        fillSubfieldsRecursively(returnable_.get(i), children.get(i), (SelectionSet) child_);
                    }
                }

                returnable.put(field.getName(), returnable_);

            } catch (Exception e) {
                returnable.put(field.getName(), new ArrayList<>());
            }
        }
    }


    /**
     * Parses genericValue to hashMap with null fields within.
     **/
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

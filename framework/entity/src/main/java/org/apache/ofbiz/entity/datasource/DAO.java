package org.apache.ofbiz.entity.datasource;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntity;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.jdbc.SQLProcessor;
import org.apache.ofbiz.entity.model.ModelEntity;
import org.apache.ofbiz.entity.model.ModelField;
import org.apache.ofbiz.entity.util.EntityFindOptions;
import org.apache.ofbiz.entity.util.EntityListIterator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DAO {

    int insert(GenericEntity entity) throws GenericEntityException;

    int updateAll(GenericEntity entity) throws GenericEntityException;

    int update(GenericEntity entity) throws GenericEntityException;

    int updateByCondition(Delegator delegator, ModelEntity modelEntity, Map<String, ? extends Object> fieldsToSet, EntityCondition condition) throws GenericEntityException;

    int updateByCondition(ModelEntity modelEntity, Map<String, ? extends Object> fieldsToSet, EntityCondition condition, SQLProcessor sqlP) throws GenericEntityException;

    void select(GenericEntity entity) throws GenericEntityException;

    void partialSelect(GenericEntity entity, Set<String> keys) throws GenericEntityException;

    /**
     * Finds GenericValues by the conditions specified in the EntityCondition object, the the EntityCondition javadoc for more details.
     *
     * @param modelEntity           The ModelEntity of the Entity as defined in the entity XML file
     * @param whereEntityCondition  The EntityCondition object that specifies how to constrain this query before any groupings are done (if this is a view entity with group-by aliases)
     * @param havingEntityCondition The EntityCondition object that specifies how to constrain this query after any groupings are done (if this is a view entity with group-by aliases)
     * @param fieldsToSelect        The fields of the named entity to get from the database; if empty or null all fields will be retreived
     * @param orderBy               The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     * @param findOptions           An instance of EntityFindOptions that specifies advanced query options. See the EntityFindOptions JavaDoc for more details.
     * @return EntityListIterator representing the result of the query: NOTE THAT THIS MUST BE CLOSED WHEN YOU ARE
     * DONE WITH IT (preferably in a finally block),
     * AND DON'T LEAVE IT OPEN TOO LONG BECAUSE IT WILL MAINTAIN A DATABASE CONNECTION.
     */
    EntityListIterator selectListIteratorByCondition(Delegator delegator, ModelEntity modelEntity, EntityCondition whereEntityCondition,
                                                     EntityCondition havingEntityCondition, Collection<String> fieldsToSelect, List<String> orderBy, EntityFindOptions findOptions)
            throws GenericEntityException;


    long selectCountByCondition(Delegator delegator, ModelEntity modelEntity, EntityCondition whereEntityCondition, EntityCondition havingEntityCondition, List<ModelField> selectFields, EntityFindOptions findOptions) throws GenericEntityException;

    int delete(GenericEntity entity) throws GenericEntityException;

    int delete(GenericEntity entity, SQLProcessor sqlP) throws GenericEntityException;

    int deleteByCondition(Delegator delegator, ModelEntity modelEntity, EntityCondition condition) throws GenericEntityException;

    int deleteByCondition(ModelEntity modelEntity, EntityCondition condition, SQLProcessor sqlP) throws GenericEntityException;

    void checkDb(Map<String, ModelEntity> modelEntities, List<String> messages, boolean addMissing);

    /**
     * Creates a list of ModelEntity objects based on meta data from the database
     */
    List<ModelEntity> induceModelFromDb(Collection<String> messages);
}


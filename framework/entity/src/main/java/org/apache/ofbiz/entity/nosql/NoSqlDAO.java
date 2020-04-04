package org.apache.ofbiz.entity.nosql;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntity;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.datasource.DAO;
import org.apache.ofbiz.entity.jdbc.SQLProcessor;
import org.apache.ofbiz.entity.model.ModelEntity;
import org.apache.ofbiz.entity.model.ModelField;
import org.apache.ofbiz.entity.util.EntityFindOptions;
import org.apache.ofbiz.entity.util.EntityListIterator;
import org.apache.commons.lang.NotImplementedException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NoSqlDAO implements DAO {
    @Override
    public int insert(GenericEntity entity) throws GenericEntityException {
        throw new NotImplementedException();
    }


    @Override
    public int updateAll(GenericEntity entity) throws GenericEntityException {
        throw new NotImplementedException();
    }

    @Override
    public int update(GenericEntity entity) throws GenericEntityException {
        throw new NotImplementedException();
    }

    @Override
    public int updateByCondition(Delegator delegator, ModelEntity modelEntity, Map<String, ?> fieldsToSet, EntityCondition condition) throws GenericEntityException {
        throw new NotImplementedException();
    }

    @Override
    public int updateByCondition(ModelEntity modelEntity, Map<String, ?> fieldsToSet, EntityCondition condition, SQLProcessor sqlP) throws GenericEntityException {
        throw new NotImplementedException();
    }

    @Override
    public void select(GenericEntity entity) throws GenericEntityException {
        throw new NotImplementedException();
    }

    @Override
    public void partialSelect(GenericEntity entity, Set<String> keys) throws GenericEntityException {
        throw new NotImplementedException();
    }

    @Override
    public EntityListIterator selectListIteratorByCondition(Delegator delegator, ModelEntity modelEntity, EntityCondition whereEntityCondition, EntityCondition havingEntityCondition, Collection<String> fieldsToSelect, List<String> orderBy, EntityFindOptions findOptions) throws GenericEntityException {
        throw new NotImplementedException();
    }

    @Override
    public long selectCountByCondition(Delegator delegator, ModelEntity modelEntity, EntityCondition whereEntityCondition, EntityCondition havingEntityCondition, List<ModelField> selectFields, EntityFindOptions findOptions) throws GenericEntityException {
        throw new NotImplementedException();
    }

    @Override
    public int delete(GenericEntity entity) throws GenericEntityException {
        throw new NotImplementedException();
    }

    @Override
    public int delete(GenericEntity entity, SQLProcessor sqlP) throws GenericEntityException {
        throw new NotImplementedException();
    }

    @Override
    public int deleteByCondition(Delegator delegator, ModelEntity modelEntity, EntityCondition condition) throws GenericEntityException {
        throw new NotImplementedException();
    }

    @Override
    public int deleteByCondition(ModelEntity modelEntity, EntityCondition condition, SQLProcessor sqlP) throws GenericEntityException {
        throw new NotImplementedException();
    }

    @Override
    public void checkDb(Map<String, ModelEntity> modelEntities, List<String> messages, boolean addMissing) {
        throw new NotImplementedException();
    }

    @Override
    public List<ModelEntity> induceModelFromDb(Collection<String> messages) {
        throw new NotImplementedException();
    }
}

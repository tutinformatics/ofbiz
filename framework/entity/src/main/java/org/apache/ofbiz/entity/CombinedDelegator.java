package org.apache.ofbiz.entity;

import org.apache.ofbiz.entity.cache.Cache;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.datasource.GenericHelper;
import org.apache.ofbiz.entity.datasource.GenericHelperInfo;
import org.apache.ofbiz.entity.eca.EntityEcaHandler;
import org.apache.ofbiz.entity.model.*;
import org.apache.ofbiz.entity.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public class CombinedDelegator extends GenericDelegator {

    // TODO: implement GenericDelegator functions and make them selectively use nosql delegator

    /**
     * Only allow creation through the factory method
     */
    protected CombinedDelegator() {
    }

    /**
     * Only allow creation through the factory method
     */
    protected CombinedDelegator(String delegatorFullName) throws GenericEntityException {
        super(delegatorFullName);
    }

    protected Callable<Void> createHelperCallable(String groupName) {
        return super.createHelperCallable(groupName);
    }

    protected void setDelegatorNames(String delegatorFullName) {
        super.setDelegatorNames(delegatorFullName);
    }

    public synchronized void initEntityEcaHandler() {
        super.initEntityEcaHandler();
    }

    protected EntityEcaHandler<?> createEntityEcaHandler() {
        return super.createEntityEcaHandler();
    }

    public String getDelegatorName() {
        return super.getDelegatorName();
    }

    public String getDelegatorBaseName() {
        return super.getDelegatorBaseName();
    }

    public String getDelegatorTenantId() {
        return super.getDelegatorTenantId();
    }

    public String getOriginalDelegatorName() {
        return super.getOriginalDelegatorName();
    }

    public ModelReader getModelReader() {
        return super.getModelReader();
    }

    public ModelGroupReader getModelGroupReader() {
        return super.getModelGroupReader();
    }

    public ModelEntity getModelEntity(String entityName) {
        return super.getModelEntity(entityName);
    }

    public String getEntityGroupName(String entityName) {
        return super.getEntityGroupName(entityName);
    }

    public Map<String, ModelEntity> getModelEntityMapByGroup(String groupName) throws GenericEntityException {
        return super.getModelEntityMapByGroup(groupName);
    }

    public String getGroupHelperName(String groupName) {
        return super.getGroupHelperName(groupName);
    }

    public GenericHelperInfo getGroupHelperInfo(String entityGroupName) {
        return super.getGroupHelperInfo(entityGroupName);
    }

    protected GenericHelperInfo getEntityHelperInfo(String entityName) {
        return super.getEntityHelperInfo(entityName);
    }

    public String getEntityHelperName(String entityName) {
        return super.getEntityHelperName(entityName);
    }

    public String getEntityHelperName(ModelEntity entity) {
        return super.getEntityHelperName(entity);
    }

    public GenericHelper getEntityHelper(String entityName) throws GenericEntityException {
        return super.getEntityHelper(entityName);
    }

    public GenericHelper getEntityHelper(ModelEntity entity) throws GenericEntityException {
        return super.getEntityHelper(entity);
    }

    public ModelFieldType getEntityFieldType(ModelEntity entity, String type) throws GenericEntityException {
        return super.getEntityFieldType(entity, type);
    }

    public ModelFieldTypeReader getModelFieldTypeReader(ModelEntity entity) {
        return super.getModelFieldTypeReader(entity);
    }

    public GenericValue makeValue(String entityName) {
        return super.makeValue(entityName);
    }

    public GenericValue makeValue(String entityName, Object... fields) {
        return super.makeValue(entityName, fields);
    }

    public GenericValue makeValue(String entityName, Map<String, ?> fields) {
        return super.makeValue(entityName, fields);
    }

    public GenericValue makeValueSingle(String entityName, Object singlePkValue) {
        return super.makeValueSingle(entityName, singlePkValue);
    }

    public GenericValue makeValidValue(String entityName, Object... fields) {
        return super.makeValidValue(entityName, fields);
    }

    public GenericValue makeValidValue(String entityName, Map<String, ?> fields) {
        return super.makeValidValue(entityName, fields);
    }

    public GenericPK makePK(String entityName) {
        return super.makePK(entityName);
    }

    public GenericPK makePK(String entityName, Object... fields) {
        return super.makePK(entityName, fields);
    }

    public GenericPK makePK(String entityName, Map<String, ?> fields) {
        return super.makePK(entityName, fields);
    }

    public GenericPK makePKSingle(String entityName, Object singlePkValue) {
        return super.makePKSingle(entityName, singlePkValue);
    }

    public GenericValue create(GenericPK primaryKey) throws GenericEntityException {
        return super.create(primaryKey);
    }

    public GenericValue create(String entityName, Object... fields) throws GenericEntityException {
        return super.create(entityName, fields);
    }

    public GenericValue create(String entityName, Map<String, ?> fields) throws GenericEntityException {
        return super.create(entityName, fields);
    }

    public GenericValue createSingle(String entityName, Object singlePkValue) throws GenericEntityException {
        return super.createSingle(entityName, singlePkValue);
    }

    public GenericValue createSetNextSeqId(GenericValue value) throws GenericEntityException {
        return super.createSetNextSeqId(value);
    }

    public GenericValue create(GenericValue value) throws GenericEntityException {
        return super.create(value);
    }

    public GenericValue createOrStore(GenericValue value) throws GenericEntityException {
        return super.createOrStore(value);
    }

    protected void saveEntitySyncRemoveInfo(GenericEntity dummyPK) throws GenericEntityException {
        super.saveEntitySyncRemoveInfo(dummyPK);
    }

    public int removeByPrimaryKey(GenericPK primaryKey) throws GenericEntityException {
        return super.removeByPrimaryKey(primaryKey);
    }

    public int removeValue(GenericValue value) throws GenericEntityException {
        return super.removeValue(value);
    }

    public int removeByAnd(String entityName, Object... fields) throws GenericEntityException {
        return super.removeByAnd(entityName, fields);
    }

    public int removeByAnd(String entityName, Map<String, ?> fields) throws GenericEntityException {
        return super.removeByAnd(entityName, fields);
    }

    public int removeByCondition(String entityName, EntityCondition condition) throws GenericEntityException {
        return super.removeByCondition(entityName, condition);
    }

    public int removeRelated(String relationName, GenericValue value) throws GenericEntityException {
        return super.removeRelated(relationName, value);
    }

    public void refresh(GenericValue value) throws GenericEntityException {
        super.refresh(value);
    }

    public void refreshFromCache(GenericValue value) throws GenericEntityException {
        super.refreshFromCache(value);
    }

    public int storeByCondition(String entityName, Map<String, ?> fieldsToSet, EntityCondition condition) throws GenericEntityException {
        return super.storeByCondition(entityName, fieldsToSet, condition);
    }

    public int store(GenericValue value) throws GenericEntityException {
        return super.store(value);
    }

    public int storeAll(List<GenericValue> values) throws GenericEntityException {
        return super.storeAll(values);
    }

    public int storeAll(List<GenericValue> values, EntityStoreOptions storeOptions) throws GenericEntityException {
        return super.storeAll(values, storeOptions);
    }

    public int removeAll(String entityName) throws GenericEntityException {
        return super.removeAll(entityName);
    }

    public int removeAll(List<? extends GenericEntity> dummyPKs) throws GenericEntityException {
        return super.removeAll(dummyPKs);
    }

    public GenericValue findOne(String entityName, boolean useCache, Object... fields) throws GenericEntityException {
        return super.findOne(entityName, useCache, fields);
    }

    public GenericValue findOne(String entityName, Map<String, ?> fields, boolean useCache) throws GenericEntityException {
        return super.findOne(entityName, fields, useCache);
    }

    public GenericValue findByPrimaryKeyPartial(GenericPK primaryKey, Set<String> keys) throws GenericEntityException {
        return super.findByPrimaryKeyPartial(primaryKey, keys);
    }

    /**
     * Finds all Generic entities
     *
     * @param entityName The Name of the Entity as defined in the entity XML file
     * @param useCache   @see Delegator#findAll( String, boolean)
     */
    public List<GenericValue> findAll(String entityName, boolean useCache) throws GenericEntityException {
        return super.findAll(entityName, useCache);
    }

    public List<GenericValue> findByAnd(String entityName, Map<String, ?> fields, List<String> orderBy, boolean useCache) throws GenericEntityException {
        return super.findByAnd(entityName, fields, orderBy, useCache);
    }

    public EntityListIterator find(String entityName, EntityCondition whereEntityCondition, EntityCondition havingEntityCondition, Set<String> fieldsToSelect, List<String> orderBy, EntityFindOptions findOptions) throws GenericEntityException {
        return super.find(entityName, whereEntityCondition, havingEntityCondition, fieldsToSelect, orderBy, findOptions);
    }

    public List<GenericValue> findList(String entityName, EntityCondition entityCondition, Set<String> fieldsToSelect, List<String> orderBy, EntityFindOptions findOptions, boolean useCache) throws GenericEntityException {
        return super.findList(entityName, entityCondition, fieldsToSelect, orderBy, findOptions, useCache);
    }

    public EntityListIterator findListIteratorByCondition(DynamicViewEntity dynamicViewEntity, EntityCondition whereEntityCondition, EntityCondition havingEntityCondition, Collection<String> fieldsToSelect, List<String> orderBy, EntityFindOptions findOptions) throws GenericEntityException {
        return super.findListIteratorByCondition(dynamicViewEntity, whereEntityCondition, havingEntityCondition, fieldsToSelect, orderBy, findOptions);
    }

    public long findCountByCondition(String entityName, EntityCondition whereEntityCondition, EntityCondition havingEntityCondition, EntityFindOptions findOptions) throws GenericEntityException {
        return super.findCountByCondition(entityName, whereEntityCondition, havingEntityCondition, findOptions);
    }

    public List<GenericValue> getMultiRelation(GenericValue value, String relationNameOne, String relationNameTwo, List<String> orderBy) throws GenericEntityException {
        return super.getMultiRelation(value, relationNameOne, relationNameTwo, orderBy);
    }

    public List<GenericValue> getRelated(String relationName, Map<String, ?> byAndFields, List<String> orderBy, GenericValue value, boolean useCache) throws GenericEntityException {
        return super.getRelated(relationName, byAndFields, orderBy, value, useCache);
    }

    public GenericPK getRelatedDummyPK(String relationName, Map<String, ?> byAndFields, GenericValue value) throws GenericEntityException {
        return super.getRelatedDummyPK(relationName, byAndFields, value);
    }

    public GenericValue getRelatedOne(String relationName, GenericValue value, boolean useCache) throws GenericEntityException {
        return super.getRelatedOne(relationName, value, useCache);
    }

    public void clearAllCaches() {
        super.clearAllCaches();
    }

    public void clearAllCaches(boolean distribute) {
        super.clearAllCaches(distribute);
    }

    public void clearCacheLine(String entityName) {
        super.clearCacheLine(entityName);
    }

    public void clearCacheLine(String entityName, Object... fields) {
        super.clearCacheLine(entityName, fields);
    }

    public void clearCacheLine(String entityName, Map<String, ?> fields) {
        super.clearCacheLine(entityName, fields);
    }

    public void clearCacheLineFlexible(GenericEntity dummyPK) {
        super.clearCacheLineFlexible(dummyPK);
    }

    public void clearCacheLineFlexible(GenericEntity dummyPK, boolean distribute) {
        super.clearCacheLineFlexible(dummyPK, distribute);
    }

    public void clearCacheLineByCondition(String entityName, EntityCondition condition) {
        super.clearCacheLineByCondition(entityName, condition);
    }

    public void clearCacheLineByCondition(String entityName, EntityCondition condition, boolean distribute) {
        super.clearCacheLineByCondition(entityName, condition, distribute);
    }

    public void clearCacheLine(GenericPK primaryKey) {
        super.clearCacheLine(primaryKey);
    }

    public void clearCacheLine(GenericPK primaryKey, boolean distribute) {
        super.clearCacheLine(primaryKey, distribute);
    }

    public void clearCacheLine(GenericValue value) {
        super.clearCacheLine(value);
    }

    public void clearCacheLine(GenericValue value, boolean distribute) {
        super.clearCacheLine(value, distribute);
    }

    public void clearAllCacheLinesByDummyPK(Collection<GenericPK> dummyPKs) {
        super.clearAllCacheLinesByDummyPK(dummyPKs);
    }

    public void clearAllCacheLinesByValue(Collection<GenericValue> values) {
        super.clearAllCacheLinesByValue(values);
    }

    public GenericValue getFromPrimaryKeyCache(GenericPK primaryKey) {
        return super.getFromPrimaryKeyCache(primaryKey);
    }

    public void putInPrimaryKeyCache(GenericPK primaryKey, GenericValue value) {
        super.putInPrimaryKeyCache(primaryKey, value);
    }

    public void putAllInPrimaryKeyCache(List<GenericValue> values) {
        super.putAllInPrimaryKeyCache(values);
    }

    public void setDistributedCacheClear(DistributedCacheClear distributedCacheClear) {
        super.setDistributedCacheClear(distributedCacheClear);
    }

    public List<GenericValue> readXmlDocument(URL url) throws SAXException, ParserConfigurationException, IOException {
        return super.readXmlDocument(url);
    }

    public List<GenericValue> makeValues(Document document) {
        return super.makeValues(document);
    }

    public GenericPK makePK(Element element) {
        return super.makePK(element);
    }

    public GenericValue makeValue(Element element) {
        return super.makeValue(element);
    }

    protected EntityEcaRuleRunner<?> getEcaRuleRunner(String entityName) {
        return super.getEcaRuleRunner(entityName);
    }

    public <T> void setEntityEcaHandler(EntityEcaHandler<T> entityEcaHandler) {
        super.setEntityEcaHandler(entityEcaHandler);
    }

    public <T> EntityEcaHandler<T> getEntityEcaHandler() {
        return super.getEntityEcaHandler();
    }

    public String getNextSeqId(String seqName) {
        return super.getNextSeqId(seqName);
    }

    public String getNextSeqId(String seqName, long staggerMax) {
        return super.getNextSeqId(seqName, staggerMax);
    }

    public Long getNextSeqIdLong(String seqName) {
        return super.getNextSeqIdLong(seqName);
    }

    public Long getNextSeqIdLong(String seqName, long staggerMax) {
        return super.getNextSeqIdLong(seqName, staggerMax);
    }

    public void setSequencer(SequenceUtil sequencer) {
        super.setSequencer(sequencer);
    }

    public void refreshSequencer() {
        super.refreshSequencer();
    }

    public void setNextSubSeqId(GenericValue value, String seqFieldName, int numericPadding, int incrementBy) {
        super.setNextSubSeqId(value, seqFieldName, numericPadding, incrementBy);
    }

    public Object encryptFieldValue(String entityName, ModelField.EncryptMethod encryptMethod, Object fieldValue) throws EntityCryptoException {
        return super.encryptFieldValue(entityName, encryptMethod, fieldValue);
    }

    public Object decryptFieldValue(String entityName, ModelField.EncryptMethod encryptMethod, String encValue) throws EntityCryptoException {
        return super.decryptFieldValue(entityName, encryptMethod, encValue);
    }

    public void setEntityCrypto(EntityCrypto crypto) {
        super.setEntityCrypto(crypto);
    }

    protected void absorbList(List<GenericValue> lst) {
        super.absorbList(lst);
    }

    public Cache getCache() {
        return super.getCache();
    }

    protected void createEntityAuditLogAll(GenericValue value, boolean isUpdate, boolean isRemove) throws GenericEntityException {
        super.createEntityAuditLogAll(value, isUpdate, isRemove);
    }

    protected void createEntityAuditLogSingle(GenericValue value, ModelField mf, boolean isUpdate, boolean isRemove, Timestamp nowTimestamp) throws GenericEntityException {
        super.createEntityAuditLogSingle(value, mf, isUpdate, isRemove, nowTimestamp);
    }

    public Delegator cloneDelegator(String delegatorFullName) {
        return super.cloneDelegator(delegatorFullName);
    }

    public Delegator cloneDelegator() {
        return super.cloneDelegator();
    }

    public Delegator makeTestDelegator(String delegatorName) {
        return super.makeTestDelegator(delegatorName);
    }

    public void rollback() {
        super.rollback();
    }

    public void initDistributedCacheClear() {
        super.initDistributedCacheClear();
    }

    protected DistributedCacheClear createDistributedCacheClear() {
        return super.createDistributedCacheClear();
    }

    protected DistributedCacheClear getDistributedCacheClear() {
        return super.getDistributedCacheClear();
    }

    public boolean useDistributedCacheClear() {
        return super.useDistributedCacheClear();
    }

    public String getCurrentSessionIdentifier() {
        return super.getCurrentSessionIdentifier();
    }

    public String getCurrentUserIdentifier() {
        return super.getCurrentUserIdentifier();
    }
}

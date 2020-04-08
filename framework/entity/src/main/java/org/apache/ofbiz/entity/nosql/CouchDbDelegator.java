package org.apache.ofbiz.entity.nosql;

import org.apache.commons.lang.NotImplementedException;
import org.apache.ofbiz.entity.*;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CouchDbDelegator extends GenericNoSqlDelegator {

  @Override
  public void clearAllCacheLinesByDummyPK(Collection<GenericPK> dummyPKs) {
    throw new NotImplementedException();
  }

  @Override
  public void clearAllCacheLinesByValue(Collection<GenericValue> values) {
    throw new NotImplementedException();
  }

  @Override
  public void clearAllCaches() {
    throw new NotImplementedException();
  }

  @Override
  public void clearAllCaches(boolean distribute) {
    throw new NotImplementedException();
  }

  @Override
  public void clearCacheLine(GenericPK primaryKey) {
    throw new NotImplementedException();
  }

  @Override
  public void clearCacheLine(GenericPK primaryKey, boolean distribute) {
    throw new NotImplementedException();
  }

  @Override
  public void clearCacheLine(GenericValue value) {
    throw new NotImplementedException();
  }

  @Override
  public void clearCacheLine(GenericValue value, boolean distribute) {
    throw new NotImplementedException();
  }

  @Override
  public void clearCacheLine(String entityName) {
    throw new NotImplementedException();
  }

  @Override
  public void clearCacheLine(String entityName, Map<String, ?> fields) {
    throw new NotImplementedException();
  }

  @Override
  public void clearCacheLine(String entityName, Object... fields) {
    throw new NotImplementedException();
  }

  @Override
  public void clearCacheLineByCondition(String entityName, EntityCondition condition) {
    throw new NotImplementedException();
  }

  @Override
  public void clearCacheLineByCondition(String entityName, EntityCondition condition, boolean distribute) {
    throw new NotImplementedException();
  }

  @Override
  public void clearCacheLineFlexible(GenericEntity dummyPK) {
    throw new NotImplementedException();
  }

  @Override
  public void clearCacheLineFlexible(GenericEntity dummyPK, boolean distribute) {
    throw new NotImplementedException();
  }

  @Override
  public Delegator cloneDelegator() {
    throw new NotImplementedException();
  }

  @Override
  public Delegator cloneDelegator(String delegatorName) {
    throw new NotImplementedException();
  }

  @Override
  public GenericValue create(GenericPK primaryKey) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public GenericValue create(GenericValue value) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public GenericValue create(String entityName, Map<String, ?> fields) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public GenericValue create(String entityName, Object... fields) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public GenericValue createOrStore(GenericValue value) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public GenericValue createSetNextSeqId(GenericValue value) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public GenericValue createSingle(String entityName, Object singlePkValue) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public Object decryptFieldValue(String entityName, ModelField.EncryptMethod encryptMethod, String encValue) throws EntityCryptoException {
    throw new NotImplementedException();
  }

  @Override
  public Object encryptFieldValue(String entityName, ModelField.EncryptMethod encryptMethod, Object fieldValue) throws EntityCryptoException {
    throw new NotImplementedException();
  }

  @Override
  public EntityListIterator find(String entityName, EntityCondition whereEntityCondition, EntityCondition havingEntityCondition, Set<String> fieldsToSelect, List<String> orderBy, EntityFindOptions findOptions) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public List<GenericValue> findAll(String entityName, boolean useCache) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public List<GenericValue> findByAnd(String entityName, Map<String, ?> fields, List<String> orderBy, boolean useCache) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public GenericValue findByPrimaryKeyPartial(GenericPK primaryKey, Set<String> keys) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public long findCountByCondition(String entityName, EntityCondition whereEntityCondition, EntityCondition havingEntityCondition, EntityFindOptions findOptions) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public List<GenericValue> findList(String entityName, EntityCondition entityCondition, Set<String> fieldsToSelect, List<String> orderBy, EntityFindOptions findOptions, boolean useCache) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public EntityListIterator findListIteratorByCondition(DynamicViewEntity dynamicViewEntity, EntityCondition whereEntityCondition, EntityCondition havingEntityCondition, Collection<String> fieldsToSelect, List<String> orderBy, EntityFindOptions findOptions) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public GenericValue findOne(String entityName, boolean useCache, Object... fields) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public GenericValue findOne(String entityName, Map<String, ?> fields, boolean useCache) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public Cache getCache() {
    throw new NotImplementedException();
  }

  @Override
  public String getCurrentSessionIdentifier() {
    throw new NotImplementedException();
  }

  @Override
  public String getCurrentUserIdentifier() {
    throw new NotImplementedException();
  }

  @Override
  public String getDelegatorName() {
    throw new NotImplementedException();
  }

  @Override
  public String getDelegatorBaseName() {
    throw new NotImplementedException();
  }

  @Override
  public String getDelegatorTenantId() {
    throw new NotImplementedException();
  }

  @Override
  public <T> EntityEcaHandler<T> getEntityEcaHandler() {
    throw new NotImplementedException();
  }

  @Override
  public ModelFieldType getEntityFieldType(ModelEntity entity, String type) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public String getEntityGroupName(String entityName) {
    throw new NotImplementedException();
  }

  @Override
  public GenericHelper getEntityHelper(ModelEntity entity) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public GenericHelper getEntityHelper(String entityName) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public String getEntityHelperName(ModelEntity entity) {
    throw new NotImplementedException();
  }

  @Override
  public String getEntityHelperName(String entityName) {
    throw new NotImplementedException();
  }

  @Override
  public GenericValue getFromPrimaryKeyCache(GenericPK primaryKey) {
    throw new NotImplementedException();
  }

  @Override
  public String getGroupHelperName(String groupName) {
    throw new NotImplementedException();
  }

  @Override
  public GenericHelperInfo getGroupHelperInfo(String entityGroupName) {
    throw new NotImplementedException();
  }

  @Override
  public ModelEntity getModelEntity(String entityName) {
    throw new NotImplementedException();
  }

  @Override
  public Map<String, ModelEntity> getModelEntityMapByGroup(String groupName) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public ModelFieldTypeReader getModelFieldTypeReader(ModelEntity entity) {
    throw new NotImplementedException();
  }

  @Override
  public ModelGroupReader getModelGroupReader() {
    throw new NotImplementedException();
  }

  @Override
  public ModelReader getModelReader() {
    throw new NotImplementedException();
  }

  @Override
  public List<GenericValue> getMultiRelation(GenericValue value, String relationNameOne, String relationNameTwo, List<String> orderBy) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public String getNextSeqId(String seqName) {
    throw new NotImplementedException();
  }

  @Override
  public String getNextSeqId(String seqName, long staggerMax) {
    throw new NotImplementedException();
  }

  @Override
  public Long getNextSeqIdLong(String seqName) {
    throw new NotImplementedException();
  }

  @Override
  public Long getNextSeqIdLong(String seqName, long staggerMax) {
    throw new NotImplementedException();
  }

  @Override
  public String getOriginalDelegatorName() {
    throw new NotImplementedException();
  }

  @Override
  public List<GenericValue> getRelated(String relationName, Map<String, ?> byAndFields, List<String> orderBy, GenericValue value, boolean useCache) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public GenericPK getRelatedDummyPK(String relationName, Map<String, ?> byAndFields, GenericValue value) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public GenericValue getRelatedOne(String relationName, GenericValue value, boolean useCache) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public void initEntityEcaHandler() {
    throw new NotImplementedException();
  }

  @Override
  public void initDistributedCacheClear() {
    throw new NotImplementedException();
  }

  @Override
  public GenericPK makePK(Element element) {
    throw new NotImplementedException();
  }

  @Override
  public GenericPK makePK(String entityName) {
    throw new NotImplementedException();
  }

  @Override
  public GenericPK makePK(String entityName, Map<String, ?> fields) {
    throw new NotImplementedException();
  }

  @Override
  public GenericPK makePK(String entityName, Object... fields) {
    throw new NotImplementedException();
  }

  @Override
  public GenericPK makePKSingle(String entityName, Object singlePkValue) {
    throw new NotImplementedException();
  }

  @Override
  public Delegator makeTestDelegator(String delegatorName) {
    throw new NotImplementedException();
  }

  @Override
  public GenericValue makeValidValue(String entityName, Map<String, ?> fields) {
    throw new NotImplementedException();
  }

  @Override
  public GenericValue makeValidValue(String entityName, Object... fields) {
    throw new NotImplementedException();
  }

  @Override
  public GenericValue makeValue(Element element) {
    throw new NotImplementedException();
  }

  @Override
  public GenericValue makeValue(String entityName) {
    throw new NotImplementedException();
  }

  @Override
  public GenericValue makeValue(String entityName, Map<String, ?> fields) {
    throw new NotImplementedException();
  }

  @Override
  public GenericValue makeValue(String entityName, Object... fields) {
    throw new NotImplementedException();
  }

  @Override
  public List<GenericValue> makeValues(Document document) {
    throw new NotImplementedException();
  }

  @Override
  public GenericValue makeValueSingle(String entityName, Object singlePkValue) {
    throw new NotImplementedException();
  }

  @Override
  public void putAllInPrimaryKeyCache(List<GenericValue> values) {
    throw new NotImplementedException();
  }

  @Override
  public void putInPrimaryKeyCache(GenericPK primaryKey, GenericValue value) {
    throw new NotImplementedException();
  }

  @Override
  public List<GenericValue> readXmlDocument(URL url) throws SAXException, ParserConfigurationException, IOException {
    throw new NotImplementedException();
  }

  @Override
  public void refresh(GenericValue value) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public void refreshFromCache(GenericValue value) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public void refreshSequencer() {
    throw new NotImplementedException();
  }

  @Override
  public int removeAll(List<? extends GenericEntity> dummyPKs) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public int removeAll(String entityName) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public int removeByAnd(String entityName, Map<String, ?> fields) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public int removeByAnd(String entityName, Object... fields) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public int removeByCondition(String entityName, EntityCondition condition) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public int removeByPrimaryKey(GenericPK primaryKey) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public int removeRelated(String relationName, GenericValue value) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public int removeValue(GenericValue value) throws GenericEntityException {
    throw new NotImplementedException();
  }

  @Override
  public void rollback() {
    throw new NotImplementedException();
  }

  @Override
  public void setDistributedCacheClear(DistributedCacheClear distributedCacheClear) {
    throw new NotImplementedException();
  }

  @Override
  public void setEntityCrypto(EntityCrypto crypto) {
    throw new NotImplementedException();
  }

  @Override
  public <T> void setEntityEcaHandler(EntityEcaHandler<T> entityEcaHandler) {
    throw new NotImplementedException();
  }

  @Override
  public void setNextSubSeqId(GenericValue value, String seqFieldName, int numericPadding, int incrementBy) {
    throw new NotImplementedException();
  }

  @Override
  public void setSequencer(SequenceUtil sequencer) {
    throw new NotImplementedException();
  }

  @Override
  public int store(GenericValue value) throws GenericEntityException {
    // TODO: implement?
    throw new NotImplementedException();
  }

  @Override
  public int storeAll(List<GenericValue> values) throws GenericEntityException {
    // TODO: implement?
    throw new NotImplementedException();
  }

  @Override
  public int storeAll(List<GenericValue> values, EntityStoreOptions storeOptions) throws GenericEntityException {
    // TODO: implement?
    throw new NotImplementedException();
  }

  @Override
  public int storeByCondition(String entityName, Map<String, ?> fieldsToSet, EntityCondition condition) throws GenericEntityException {
    // TODO: implement?
    throw new NotImplementedException();
  }

  @Override
  public boolean useDistributedCacheClear() {
    throw new NotImplementedException();
  }

}

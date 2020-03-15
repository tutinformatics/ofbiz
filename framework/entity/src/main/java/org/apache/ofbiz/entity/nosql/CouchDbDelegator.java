package org.apache.ofbiz.entity.nosql;

import org.apache.ofbiz.entity.*;
import org.apache.ofbiz.entity.cache.Cache;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.datasource.GenericHelper;
import org.apache.ofbiz.entity.datasource.GenericHelperInfo;
import org.apache.ofbiz.entity.eca.EntityEcaHandler;
import org.apache.ofbiz.entity.model.*;
import org.apache.ofbiz.entity.nosql.GenericNoSqlDelegator;
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

  }

  @Override
  public void clearAllCacheLinesByValue(Collection<GenericValue> values) {

  }

  @Override
  public void clearAllCaches() {

  }

  @Override
  public void clearAllCaches(boolean distribute) {

  }

  @Override
  public void clearCacheLine(GenericPK primaryKey) {

  }

  @Override
  public void clearCacheLine(GenericPK primaryKey, boolean distribute) {

  }

  @Override
  public void clearCacheLine(GenericValue value) {

  }

  @Override
  public void clearCacheLine(GenericValue value, boolean distribute) {

  }

  @Override
  public void clearCacheLine(String entityName) {

  }

  @Override
  public void clearCacheLine(String entityName, Map<String, ?> fields) {

  }

  @Override
  public void clearCacheLine(String entityName, Object... fields) {

  }

  @Override
  public void clearCacheLineByCondition(String entityName, EntityCondition condition) {

  }

  @Override
  public void clearCacheLineByCondition(String entityName, EntityCondition condition, boolean distribute) {

  }

  @Override
  public void clearCacheLineFlexible(GenericEntity dummyPK) {

  }

  @Override
  public void clearCacheLineFlexible(GenericEntity dummyPK, boolean distribute) {

  }

  @Override
  public Delegator cloneDelegator() {
    return null;
  }

  @Override
  public Delegator cloneDelegator(String delegatorName) {
    return null;
  }

  @Override
  public GenericValue create(GenericPK primaryKey) throws GenericEntityException {
    return null;
  }

  @Override
  public GenericValue create(GenericValue value) throws GenericEntityException {
    return null;
  }

  @Override
  public GenericValue create(String entityName, Map<String, ?> fields) throws GenericEntityException {
    return null;
  }

  @Override
  public GenericValue create(String entityName, Object... fields) throws GenericEntityException {
    return null;
  }

  @Override
  public GenericValue createOrStore(GenericValue value) throws GenericEntityException {
    return null;
  }

  @Override
  public GenericValue createSetNextSeqId(GenericValue value) throws GenericEntityException {
    return null;
  }

  @Override
  public GenericValue createSingle(String entityName, Object singlePkValue) throws GenericEntityException {
    return null;
  }

  @Override
  public Object decryptFieldValue(String entityName, ModelField.EncryptMethod encryptMethod, String encValue) throws EntityCryptoException {
    return null;
  }

  @Override
  public Object encryptFieldValue(String entityName, ModelField.EncryptMethod encryptMethod, Object fieldValue) throws EntityCryptoException {
    return null;
  }

  @Override
  public EntityListIterator find(String entityName, EntityCondition whereEntityCondition, EntityCondition havingEntityCondition, Set<String> fieldsToSelect, List<String> orderBy, EntityFindOptions findOptions) throws GenericEntityException {
    return null;
  }

  @Override
  public List<GenericValue> findAll(String entityName, boolean useCache) throws GenericEntityException {
    return null;
  }

  @Override
  public List<GenericValue> findByAnd(String entityName, Map<String, ?> fields, List<String> orderBy, boolean useCache) throws GenericEntityException {
    return null;
  }

  @Override
  public GenericValue findByPrimaryKeyPartial(GenericPK primaryKey, Set<String> keys) throws GenericEntityException {
    return null;
  }

  @Override
  public long findCountByCondition(String entityName, EntityCondition whereEntityCondition, EntityCondition havingEntityCondition, EntityFindOptions findOptions) throws GenericEntityException {
    return 0;
  }

  @Override
  public List<GenericValue> findList(String entityName, EntityCondition entityCondition, Set<String> fieldsToSelect, List<String> orderBy, EntityFindOptions findOptions, boolean useCache) throws GenericEntityException {
    return null;
  }

  @Override
  public EntityListIterator findListIteratorByCondition(DynamicViewEntity dynamicViewEntity, EntityCondition whereEntityCondition, EntityCondition havingEntityCondition, Collection<String> fieldsToSelect, List<String> orderBy, EntityFindOptions findOptions) throws GenericEntityException {
    return null;
  }

  @Override
  public GenericValue findOne(String entityName, boolean useCache, Object... fields) throws GenericEntityException {
    return null;
  }

  @Override
  public GenericValue findOne(String entityName, Map<String, ?> fields, boolean useCache) throws GenericEntityException {
    return null;
  }

  @Override
  public Cache getCache() {
    return null;
  }

  @Override
  public String getCurrentSessionIdentifier() {
    return null;
  }

  @Override
  public String getCurrentUserIdentifier() {
    return null;
  }

  @Override
  public String getDelegatorName() {
    return null;
  }

  @Override
  public String getDelegatorBaseName() {
    return null;
  }

  @Override
  public String getDelegatorTenantId() {
    return null;
  }

  @Override
  public <T> EntityEcaHandler<T> getEntityEcaHandler() {
    return null;
  }

  @Override
  public ModelFieldType getEntityFieldType(ModelEntity entity, String type) throws GenericEntityException {
    return null;
  }

  @Override
  public String getEntityGroupName(String entityName) {
    return null;
  }

  @Override
  public GenericHelper getEntityHelper(ModelEntity entity) throws GenericEntityException {
    return null;
  }

  @Override
  public GenericHelper getEntityHelper(String entityName) throws GenericEntityException {
    return null;
  }

  @Override
  public String getEntityHelperName(ModelEntity entity) {
    return null;
  }

  @Override
  public String getEntityHelperName(String entityName) {
    return null;
  }

  @Override
  public GenericValue getFromPrimaryKeyCache(GenericPK primaryKey) {
    return null;
  }

  @Override
  public String getGroupHelperName(String groupName) {
    return null;
  }

  @Override
  public GenericHelperInfo getGroupHelperInfo(String entityGroupName) {
    return null;
  }

  @Override
  public ModelEntity getModelEntity(String entityName) {
    return null;
  }

  @Override
  public Map<String, ModelEntity> getModelEntityMapByGroup(String groupName) throws GenericEntityException {
    return null;
  }

  @Override
  public ModelFieldTypeReader getModelFieldTypeReader(ModelEntity entity) {
    return null;
  }

  @Override
  public ModelGroupReader getModelGroupReader() {
    return null;
  }

  @Override
  public ModelReader getModelReader() {
    return null;
  }

  @Override
  public List<GenericValue> getMultiRelation(GenericValue value, String relationNameOne, String relationNameTwo, List<String> orderBy) throws GenericEntityException {
    return null;
  }

  @Override
  public String getNextSeqId(String seqName) {
    return null;
  }

  @Override
  public String getNextSeqId(String seqName, long staggerMax) {
    return null;
  }

  @Override
  public Long getNextSeqIdLong(String seqName) {
    return null;
  }

  @Override
  public Long getNextSeqIdLong(String seqName, long staggerMax) {
    return null;
  }

  @Override
  public String getOriginalDelegatorName() {
    return null;
  }

  @Override
  public List<GenericValue> getRelated(String relationName, Map<String, ?> byAndFields, List<String> orderBy, GenericValue value, boolean useCache) throws GenericEntityException {
    return null;
  }

  @Override
  public GenericPK getRelatedDummyPK(String relationName, Map<String, ?> byAndFields, GenericValue value) throws GenericEntityException {
    return null;
  }

  @Override
  public GenericValue getRelatedOne(String relationName, GenericValue value, boolean useCache) throws GenericEntityException {
    return null;
  }

  @Override
  public void initEntityEcaHandler() {

  }

  @Override
  public void initDistributedCacheClear() {

  }

  @Override
  public GenericPK makePK(Element element) {
    return null;
  }

  @Override
  public GenericPK makePK(String entityName) {
    return null;
  }

  @Override
  public GenericPK makePK(String entityName, Map<String, ?> fields) {
    return null;
  }

  @Override
  public GenericPK makePK(String entityName, Object... fields) {
    return null;
  }

  @Override
  public GenericPK makePKSingle(String entityName, Object singlePkValue) {
    return null;
  }

  @Override
  public Delegator makeTestDelegator(String delegatorName) {
    return null;
  }

  @Override
  public GenericValue makeValidValue(String entityName, Map<String, ?> fields) {
    return null;
  }

  @Override
  public GenericValue makeValidValue(String entityName, Object... fields) {
    return null;
  }

  @Override
  public GenericValue makeValue(Element element) {
    return null;
  }

  @Override
  public GenericValue makeValue(String entityName) {
    return null;
  }

  @Override
  public GenericValue makeValue(String entityName, Map<String, ?> fields) {
    return null;
  }

  @Override
  public GenericValue makeValue(String entityName, Object... fields) {
    return null;
  }

  @Override
  public List<GenericValue> makeValues(Document document) {
    return null;
  }

  @Override
  public GenericValue makeValueSingle(String entityName, Object singlePkValue) {
    return null;
  }

  @Override
  public void putAllInPrimaryKeyCache(List<GenericValue> values) {

  }

  @Override
  public void putInPrimaryKeyCache(GenericPK primaryKey, GenericValue value) {

  }

  @Override
  public List<GenericValue> readXmlDocument(URL url) throws SAXException, ParserConfigurationException, IOException {
    return null;
  }

  @Override
  public void refresh(GenericValue value) throws GenericEntityException {

  }

  @Override
  public void refreshFromCache(GenericValue value) throws GenericEntityException {

  }

  @Override
  public void refreshSequencer() {

  }

  @Override
  public int removeAll(List<? extends GenericEntity> dummyPKs) throws GenericEntityException {
    return 0;
  }

  @Override
  public int removeAll(String entityName) throws GenericEntityException {
    return 0;
  }

  @Override
  public int removeByAnd(String entityName, Map<String, ?> fields) throws GenericEntityException {
    return 0;
  }

  @Override
  public int removeByAnd(String entityName, Object... fields) throws GenericEntityException {
    return 0;
  }

  @Override
  public int removeByCondition(String entityName, EntityCondition condition) throws GenericEntityException {
    return 0;
  }

  @Override
  public int removeByPrimaryKey(GenericPK primaryKey) throws GenericEntityException {
    return 0;
  }

  @Override
  public int removeRelated(String relationName, GenericValue value) throws GenericEntityException {
    return 0;
  }

  @Override
  public int removeValue(GenericValue value) throws GenericEntityException {
    return 0;
  }

  @Override
  public void rollback() {

  }

  @Override
  public void setDistributedCacheClear(DistributedCacheClear distributedCacheClear) {

  }

  @Override
  public void setEntityCrypto(EntityCrypto crypto) {

  }

  @Override
  public <T> void setEntityEcaHandler(EntityEcaHandler<T> entityEcaHandler) {

  }

  @Override
  public void setNextSubSeqId(GenericValue value, String seqFieldName, int numericPadding, int incrementBy) {

  }

  @Override
  public void setSequencer(SequenceUtil sequencer) {

  }

  @Override
  public int store(GenericValue value) throws GenericEntityException {
    return 0;
  }

  @Override
  public int storeAll(List<GenericValue> values) throws GenericEntityException {
    return 0;
  }

  @Override
  public int storeAll(List<GenericValue> values, EntityStoreOptions storeOptions) throws GenericEntityException {
    return 0;
  }

  @Override
  public int storeByCondition(String entityName, Map<String, ?> fieldsToSet, EntityCondition condition) throws GenericEntityException {
    return 0;
  }

  @Override
  public boolean useDistributedCacheClear() {
    return false;
  }

}

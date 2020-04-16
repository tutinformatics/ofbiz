package org.apache.ofbiz.entity.util;

import com.github.openjson.JSONObject;
import org.apache.ofbiz.base.conversion.ConversionException;
import org.apache.ofbiz.base.conversion.ConverterLoader;
import org.apache.ofbiz.base.lang.JSON;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.model.ModelRelation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExtendedConverters implements ConverterLoader {

	@Override
	public void loadConverters() {
		org.apache.ofbiz.base.conversion.Converters.loadContainedConverters(ExtendedConverters.class);
	}

	public static class ExtendedJSONToGenericValue extends Converters.JSONToGenericValue {

		public GenericValue convert(String delegatorName, JSON obj) throws ConversionException {
			JSONObject node;
			node = new JSONObject(obj.toString());
			node.put("_DELEGATOR_NAME_", delegatorName);
			System.out.println(node.toString());
			return convert(JSON.from(node.toString()));
		}

		public GenericValue convert(String delegatorName, String entityName, JSON obj) throws ConversionException {
			JSONObject node;
			node = new JSONObject(obj.toString());
			node.put("_DELEGATOR_NAME_", delegatorName);
			node.put("_ENTITY_NAME_", entityName);
			System.out.println(node.toString());
			return convert(JSON.from(node.toString()));
		}
	}

	public static class ExtendedGenericValueToJSON extends Converters.GenericValueToJSON {

		private static final int MAX_DEPTH = 1;

		private Map<String, Object> getObjMap(GenericValue obj, int d) throws GenericEntityException {
			Map<String, Object> fieldMap = new HashMap<>(obj);
			fieldMap.put("_DELEGATOR_NAME_", obj.getDelegator().getDelegatorName());
			fieldMap.put("_ENTITY_NAME_", obj.getEntityName());

			if (d != 0) {
				List<ModelRelation> itSingular = obj.getModelEntity().getRelationsList(true, true, false);
				List<ModelRelation> itMultitude = obj.getModelEntity().getRelationsList(false, false, true);

				for (ModelRelation rel : itSingular) {
					List<GenericValue> relList = obj.getRelated(rel.getCombinedName(), null, null, false);
					if (relList.size() > 0) {
						fieldMap.put("_Related_" + rel.getCombinedName(), getObjMap(relList.get(0), d - 1));
					}
				}

				for (ModelRelation rel : itMultitude) {
					List<GenericValue> relList = obj.getRelated(rel.getCombinedName(), null, null, false);
					if (relList.size() > 0) {
						List<Map<String, Object>> relMaps = new ArrayList<>();
						for (GenericValue relObj : relList) {
							relMaps.add(getObjMap(relObj, d - 1));
						}
						fieldMap.put("_RelatedList_" + rel.getCombinedName(), relMaps);
					}
				}
			}
			return fieldMap;
		}

		public JSON convertNoNames(GenericValue obj) throws ConversionException {
			Map<String, Object> fieldMap = new HashMap<>(obj);
			try {
				return JSON.from(fieldMap);
			} catch (IOException e) {
				throw new ConversionException(e);
			}
		}

		public JSON convertWithChildren(GenericValue obj) throws ConversionException {
			try {
				return JSON.from(getObjMap(obj, MAX_DEPTH));
			} catch (IOException | GenericEntityException e) {
				throw new ConversionException(e);
			}
		}

		public JSON convertListWithChildren(List<GenericValue> obj, Integer d) throws ConversionException {
			try {
				return JSON.from(obj.stream().map(x -> {
					try {
						return getObjMap(x, d > MAX_DEPTH ? MAX_DEPTH : d);
					} catch (GenericEntityException e) {
						e.printStackTrace();
						return null;
					}
				}).collect(Collectors.toList()));
			} catch (IOException e) {
				throw new ConversionException(e);
			}
		}

		public JSON convertListWithChildren(List<GenericValue> obj) throws ConversionException {
			return this.convertListWithChildren(obj, MAX_DEPTH);
		}
	}

}

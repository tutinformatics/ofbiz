package ee.taltech.bigdata.filtering;

import org.apache.ofbiz.base.util.UtilGenerics;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.model.ModelEntity;
import org.apache.ofbiz.entity.model.ModelField;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.*;

public class FilterService {

	public static Map<String, Object> getFilterableParameters(DispatchContext dctx, Map<String, ?> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();

		String entityName = (String) context.get("entityName");
		List<String> fieldList = UtilGenerics.cast(context.get("fieldList"));

		ModelEntity model = delegator.getModelEntity(entityName);

		Map<String, Object> results = ServiceUtil.returnSuccess();
		Map<String, Object> fields = new HashMap<>();

		for (ModelField field : model.getFieldsUnmodifiable()) {
			Map<String, Object> operationsAndType = new HashMap<>();
			String type = field.getType();
			List<String> operations = new ArrayList<>();

			boolean yeet = false;
//			and, between, equals, greaterThan, greaterThanEqualTo, in, lessThan, lessThanEqualTo, like, not, notEqual, or
			switch (type) {
				case "date-time":
				case "date":
					operations = Arrays.asList("equals", "greaterThan", "greaterThanEqualTo", "lessThan", "lessThanEqualTo", "notEqual", "sameDay", "empty");
					break;
				case "time":
//					operations = Arrays.asList("equals", "greaterThan", "greaterThanEqualTo", "lessThan", "lessThanEqualTo", "notEqual", "empty");
//					break;
				case "currency-amount":
				case "currency-precise":
				case "fixed-point":
//					operations = Arrays.asList("equals", "greaterThan", "greaterThanEqualTo", "lessThan", "lessThanEqualTo", "notEqual", "empty");
//					break;
				case "floating-point":
				case "integer":
				case "numeric":
					operations = Arrays.asList("equals", "greaterThan", "greaterThanEqualTo", "lessThan", "lessThanEqualTo", "notEqual", "empty");
					break;
				case "blob":
				case "byte-array":
				case "object":
					yeet = true;
					break;
				default:
					operations = Arrays.asList("equals", "notEqual", "like", "notLike", "contains", "notContains", "empty", "in", "not-in");
					break;
			}

			if (yeet) {
				continue;
			}

			operationsAndType.put("operations", operations);
			operationsAndType.put("type", type);
			operationsAndType.put("fieldName", field.getName());
			fields.put(field.getName(), operationsAndType);
		}

		results.put("filterings", fields);
		return results;
	}
}

package ee.taltech.bigdata.filtering;

import org.apache.ofbiz.base.util.UtilGenerics;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.util.EntityListIterator;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FilteredSearchService {

	public static Map<String, Object> performFilteredSearch(DispatchContext dctx, Map<String, ?> context) throws GenericServiceException, GenericEntityException {
//		{
//			"fieldName": "name"
//			"operation": "like"
//			"value": "val"
//		  "ignoreCase": boolean
//		}
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> performFindInput = new HashMap<>();
		Map<String, Object> performFindInputFieldsInput = new HashMap<>();
		performFindInput.put("entityName", context.get("entityName"));
		performFindInput.put("inputFields", performFindInputFieldsInput);

		List<Map<String, Object>> params = UtilGenerics.cast(context.get("filterParameters"));

		Set<String> uniqueFields = params.stream().map(x -> (String) x.get("fieldName")).collect(Collectors.toSet());

		for (String field : uniqueFields) {
			List<Map<String, Object>> specifics = params.stream().filter(x -> x.get("fieldName").equals(field)).collect(Collectors.toList());
			int i = 0;
			for (Map<String, Object> specific : specifics) {
				String prefix = field + "_fld" + i++ + "_";
				performFindInputFieldsInput.put(prefix + "op", specific.get("operation"));
				performFindInputFieldsInput.put(prefix + "value", specific.get("value"));
				if (specific.containsKey("ignoreCase") && specific.get("ignoreCase").equals(true)) {
					performFindInputFieldsInput.put(prefix + "ic", "Y");
				}
			}
		}

		EntityListIterator t = UtilGenerics.cast(dispatcher.runSync("performFind", performFindInput).get("listIt"));
		Map<String, Object> result = new HashMap<>();
		result.put("result", t.getCompleteList());
		return result;
	}
}

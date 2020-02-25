package ee.taltech.accounting.connector.camel.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.*;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceDispatcher;

import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InvoiceService {

	public static final String module = InvoiceService.class.getName();
	Delegator delegator;

	public InvoiceService(Delegator delegator) {
		this.delegator = delegator;
	}

	//@Deprecated
    /*public Map<String, Object> getInvoices(DispatchContext dctx, Map<String, ?> context) {
        Delegator delegator = dctx.getDelegator();
        try {
            List<GenericValue> orderItems = EntityQuery.use(delegator)
                    .from("Invoice")
                    .queryList();
            System.out.println(orderItems);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        return ServiceUtil.returnSuccess();
    }*/

	public String getInvoices() {
		List<GenericValue> orderItems = new ArrayList<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			orderItems = EntityQuery.use(delegator)
							.from("Invoice")
							.queryList();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			GenericValue error = new GenericValue();
			error.put("Error", e);
			orderItems.add(error);
		}
		return gson.toJson(orderItems);
	}


	public String createInvoice(String json) {
//		Gson gson = new Gson();
//
//		JsonObject jsonObj = gson.fromJson(json, JsonObject.class);

		// Lets now invoke the ofbiz service that creates a product
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		LocalDispatcher dispatcher = ServiceDispatcher.getLocalDispatcher("default", delegator);
//		dispatcher.

//		Map<String, String> paramMap = null;
//		try {
//			paramMap = new ObjectMapper().readValue(json, HashMap.class);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//						UtilMisc.toMap("internalName", jsonObj.get("internalName"), "productName",
//						jsonObj.get("productName"), "productTypeId", jsonObj.get("productTypeId"));
//		String jsonThing = gson.toJson(new InvoicePojo("Company", "SALES_INVOICE", LocalDateTime.now(), "Tere", LocalDateTime.now(), "USD", "INVOICE_IN_PROGRESS", "8888", "AcctBuyer"));
//		Map<String, Object> result = new HashMap<>();
		try {
			delegator.create("Invoice", UtilMisc.toMap("partyIdFrom", "Company", "invoiceTypeId", "SALES_INVOICE", "dueDate", Timestamp.valueOf(LocalDateTime.now()),
							"description", "tere", "invoiceDate", Timestamp.valueOf(LocalDateTime.now()), "currencyUomId", "USD", "statusId", "INVOICE_PAID", "invoiceId", "8888", "partyId", "AcctBuyer"));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
//		try {
//			result = delegator.create("Invoice", gson.fromJson(jsonThing, Map.class)); //.create("Invoice", paramMap); // dispatcher.runSync("createInvoice", paramMap);
//		} catch (GenericEntityException e1) {
//			return Response.serverError().entity(e1.toString()).build().toString();
//		}

		return Response.ok().type("application/json").build().toString();

//		String productId = result.get("productId").toString();
//		String product = Util.getProduct(productId);
//		if (product != null) {
//			return Response.ok(product).type("application/json").build();
//		} else {
//			return Response.serverError().entity("Problem reading the new product after created!").build();
//		}
	}
}

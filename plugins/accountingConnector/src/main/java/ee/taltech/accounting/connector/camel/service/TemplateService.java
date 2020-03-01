package ee.taltech.accounting.connector.camel.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.ofbiz.base.conversion.ConversionException;
import org.apache.ofbiz.base.lang.JSON;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.Converters;
import org.apache.ofbiz.entity.util.EntityQuery;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class TemplateService {

	public static final String module = TemplateService.class.getName();
	Delegator delegator;

	public TemplateService(Delegator delegator) {
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


	public Response createInvoice(String json) {
		try {
			delegator.create(new Converters.JSONToGenericValue().convert("Invoice", JSON.from(json)));
			return Response.ok().type("application/json").build();
		} catch (GenericEntityException | ConversionException e) {
			e.printStackTrace();
			return Response.serverError().entity("Error of some sort").build();
		}

//		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
//		LocalDispatcher dispatcher = ServiceDispatcher.getLocalDispatcher("default", delegator);


//			delegator.create("Invoice", UtilMisc.toMap("partyIdFrom", "Company", "invoiceTypeId", "SALES_INVOICE", "dueDate", Timestamp.valueOf(LocalDateTime.now()),
//							"description", "tere", "invoiceDate", Timestamp.valueOf(LocalDateTime.now()), "currencyUomId", "USD", "statusId", "INVOICE_PAID", "invoiceId", "8888", "partyId", "AcctBuyer"));

	}
}

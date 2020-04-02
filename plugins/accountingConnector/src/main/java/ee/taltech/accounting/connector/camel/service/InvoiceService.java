package ee.taltech.accounting.connector.camel.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.util.Converters;
import org.apache.ofbiz.entity.util.EntityQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.ofbiz.base.conversion.ConversionException;
import org.apache.ofbiz.base.lang.JSON;
import javax.ws.rs.core.Response;

public class InvoiceService {

    private Delegator delegator;
    public static final Converters.JSONToGenericValue jsonToGenericConverter = new Converters.JSONToGenericValue();

    public InvoiceService(Delegator delegator) {
        this.delegator = delegator;
    }

    public static final String module = InvoiceService.class.getName();

    public String getInvoices() {
        List<GenericValue> orderItems = new ArrayList<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            orderItems = EntityQuery.use(delegator)
                    .from("Invoice")
                    .queryList();
            System.out.println(orderItems);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            GenericValue error = new GenericValue();
            error.put("Error", e);
            orderItems.add(error);
        }
        return gson.toJson(orderItems);
    }

    public String getInvoiceById(String id) {
        List<GenericValue> orderItems = new ArrayList<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            orderItems = EntityQuery.use(delegator)
                    .from("Invoice")
                    .where(EntityCondition.makeCondition("invoiceId", id))
                    .queryList();
            System.out.println(orderItems);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            GenericValue error = new GenericValue();
            error.put("Error", e);
            orderItems.add(error);
        }
        return gson.toJson(orderItems);
    }

    /**
     *
     * @param json String form of an entity
     * @author big_data / REST api team
     * @return response to say if success or not
     */
    public Response createInvoice(String json) {
        try {
            // uses custom method in the converter class that takes in delegator name, entity name and json
            // and spits out a GenericValue.
            // The converter "default" method with just GenericValue input wants the object to contain
            // _ENTITY_NAME_ and _DELEGATOR_NAME_ fields to be able to do the conversion.
            GenericValue object = jsonToGenericConverter.convert(delegator.getDelegatorName(), "Invoice", JSON.from(json));
            // incrementing the primary key ID, ofbiz takes care of it if PK is just one field
            object.setNextSeqId();
            // uses delegator's create() method that takes in a GenericValue and saves it into DB
            // it knows where to save it because genericvalue object knows what entity it is and what delegator it must use
            delegator.create(object);
            return Response.ok().type("application/json").build();
        } catch (GenericEntityException | ConversionException e) {
            e.printStackTrace();
            return Response.serverError().entity("Error of some sort").build();
        }
    }
}

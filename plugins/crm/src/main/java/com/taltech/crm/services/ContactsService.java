package com.taltech.crm.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.component.sparkrest.SparkMessage;
import org.apache.ofbiz.base.conversion.Converter;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.Converters;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.ofbiz.base.conversion.ConversionException;
import org.apache.ofbiz.base.lang.JSON;
import javax.ws.rs.core.Response;


public class ContactsService {


    private DispatchContext dctx;
    private Delegator delegator;

    public ContactsService(DispatchContext dctx) {
        this.dctx = dctx;
        delegator = dctx.getDelegator();
    }

    private String getParamValueFromExchange(String paramName, Exchange exchange) {
        SparkMessage msg = (SparkMessage) exchange.getIn();
        Map<String, String> params = msg.getRequest().params();
        String sparkParamName = ":" + paramName;
        return params.get(sparkParamName);
    }


    public List<GenericValue> getContactList() {
        try {
            return delegator.findAll("Person", true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteContact(Exchange exchange) {
        try {
            String name = getParamValueFromExchange("name", exchange);
            String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

            EntityCondition condition = EntityCondition.makeCondition(
                    "firstName", EntityOperator.EQUALS, capitalizedName);
            //delegator.removeByAnd("Person",  UtilMisc.toMap("firstName", capitalizedName));
            delegator.removeByCondition("Person", condition);

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }


    public List<GenericValue> getContactByFirstName(Exchange exchange) {
        try {
            String name = getParamValueFromExchange("name", exchange);
            //EntityEcaRuleRunner<?> ecaRunner = this.getEcaRuleRunner(modelEntity.getEntityName());
            String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            System.out.println(name);

            List<GenericValue> result = delegator.findByAnd("Person",  UtilMisc.toMap("firstName", capitalizedName),null , true);
            if (result.size() >= 1) {
                return result;
            }
            return null;
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

}

package org.apache.ofbiz.product.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BigBuyInventory {
    public static Map<String, Object> addBigBuyCategory(DispatchContext ctx, Map<String, Object> context) throws UnirestException, JsonProcessingException {

        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue)context.get("userLogin");
        Locale locale = (Locale)context.get("locale");
        String productCategoryTypeId = (String) context.get("productCategoryTypeId");


        //  Adding Catalog

        Map<String, Object> contextCopyCatalog = new HashMap<>(context);
        contextCopyCatalog.put("userLogin", userLogin);
        contextCopyCatalog.put("locale", locale);
        contextCopyCatalog.put("catalogName", "BigBuyCatalog");
        contextCopyCatalog.put("prodCatalogId", "BigBuyCatalog");

        Map<String, Object> contextCopyCategoryToCatalog = new HashMap<>(context);
        contextCopyCategoryToCatalog.put("userLogin", userLogin);
        contextCopyCategoryToCatalog.put("locale", locale);
        contextCopyCategoryToCatalog.put("prodCatalogCategoryTypeId", "PCCT_PURCH_ALLW");
        contextCopyCategoryToCatalog.put( "prodCatalogId", "BigBuyCatalog");
        contextCopyCategoryToCatalog.put("productCategoryId", productCategoryTypeId);



        try {
            dispatcher.runSync("createProdCatalog", contextCopyCatalog);
        } catch (GenericServiceException e){
            System.err.println(e.getMessage());
        }


        //  Adding Category


        Unirest.setTimeouts(0, 0);
        com.mashape.unirest.http.JsonNode response = Unirest.get("https://api.sandbox.bigbuy.eu/rest/catalog/category/"+productCategoryTypeId+".json?isoCode=en")
                .header("Authorization", "Bearer YTUwOGI5ZGY5ZDMzNGM4Mjk3ZTY4N2ExODJjYmJiN2VjZDU0ZThiM2Y2NTZkMTlhMjE4NzQ0ZTE4YjgwYjBjNA")
                .asJson().getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(String.valueOf(response));

        if (jsonNode.get("name") != null){
            context.put("categoryName", String.valueOf(jsonNode.get("name")).replace("\"",""));
        }


        //productCategoryTypeId == categoryID on BigBuy

        context.put("productCategoryId", productCategoryTypeId);
        context.put("productCategoryTypeId", "CATALOG_CATEGORY");

        //productTypeId == GOOD

        Map<String,Object> success = ServiceUtil.returnSuccess();

        try {
            dispatcher.runSync("createProductCategory", context);
        } catch (GenericServiceException e){
            System.err.println(e.getMessage());
        }



        //  Adding Category to Catalog


        try {
            dispatcher.runAsync("addProductCategoryToProdCatalog", contextCopyCategoryToCatalog);
        } catch (GenericServiceException e){
            System.err.println(e.getMessage());
        }



        return success;
    }
}

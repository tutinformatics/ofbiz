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

import java.util.*;

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
        String categoryName = String.valueOf(jsonNode.get("name")).replace("\"","");

        if (jsonNode.get("name") != null){
            context.put("categoryName", categoryName);
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
            dispatcher.runSync("addProductCategoryToProdCatalog", contextCopyCategoryToCatalog);
        } catch (GenericServiceException e){
            System.err.println(e.getMessage());
        }

        return success;
    }
    public static Map<String, Object> addProductsToCategory(DispatchContext ctx, Map<String, Object> context) throws UnirestException, JsonProcessingException {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Map<String,Object> success = ServiceUtil.returnSuccess();
        ObjectMapper objectMapper = new ObjectMapper();

        GenericValue userLogin = (GenericValue)context.get("userLogin");
        Locale locale = (Locale)context.get("locale");
        String productCategoryTypeId = (String) context.get("productCategoryTypeId");

        // Adding Products from required Category to list

        List<String> requiredProducts = new ArrayList<>();

        Unirest.setTimeouts(0, 0);
        com.mashape.unirest.http.JsonNode listWithProductId = Unirest.get("https://api.sandbox.bigbuy.eu/rest/catalog/products.json?isoCode=en")
                .header("Authorization", "Bearer YTUwOGI5ZGY5ZDMzNGM4Mjk3ZTY4N2ExODJjYmJiN2VjZDU0ZThiM2Y2NTZkMTlhMjE4NzQ0ZTE4YjgwYjBjNA").asJson().getBody();

        com.fasterxml.jackson.databind.JsonNode jsonListId = objectMapper.readTree(String.valueOf(listWithProductId));

        Unirest.setTimeouts(0, 0);
        com.mashape.unirest.http.JsonNode listWithProductName = Unirest.get("https://api.sandbox.bigbuy.eu/rest/catalog/productsinformation.json?isoCode=en")
                .header("Authorization", "Bearer YTUwOGI5ZGY5ZDMzNGM4Mjk3ZTY4N2ExODJjYmJiN2VjZDU0ZThiM2Y2NTZkMTlhMjE4NzQ0ZTE4YjgwYjBjNA").asJson().getBody();

        com.fasterxml.jackson.databind.JsonNode jsonListName = objectMapper.readTree(String.valueOf(listWithProductName));

        for (JsonNode node : jsonListId) {
            JsonNode node1 = objectMapper.readTree(String.valueOf(node));
            String catId = String.valueOf(node1.get("category"));
            String Id = String.valueOf(node1.get("id"));
            if (catId.equals(productCategoryTypeId)) {
                requiredProducts.add(Id);
            }
        }

        for (JsonNode node : jsonListName) {
            JsonNode node1 = objectMapper.readTree(String.valueOf(node));
            String name = String.valueOf(node1.get("name"));
            String id = String.valueOf(node1.get("id"));
            if (requiredProducts.contains(id)){

                //add product context
                Map<String, Object> productContext = new HashMap<>(context);
                productContext.put("userLogin", userLogin);
                productContext.put("locale", locale);

                productContext.put("internalName", name);
                productContext.put("productName", name);
                productContext.put("productId", id);
                productContext.put("productTypeId", "GOOD");
                productContext.put("requirementMethodEnumId", "PRODRQM_DS");


                //add party to product context
                Map<String, Object> partyToProductContext = new HashMap<>(context);
                productContext.put("userLogin", userLogin);
                productContext.put("locale", locale);

                partyToProductContext.put("partyId", "BigBuy");
                partyToProductContext.put("productId", id);
                partyToProductContext.put("roleTypeId", "SUPPLIER");

                //add product to category context
                Map<String, Object> productToCategoryContext = new HashMap<>(context);
                productContext.put("userLogin", userLogin);
                productContext.put("locale", locale);

                partyToProductContext.put("productCategoryId", productCategoryTypeId);
                partyToProductContext.put("productId", id);

                try {
                    dispatcher.runSync("createProduct", productContext);
                } catch (GenericServiceException e){
                    System.err.println(e.getMessage());
                }

                try {
                    dispatcher.runSync("addPartyToProduct", partyToProductContext);
                } catch (GenericServiceException e){
                    System.err.println(e.getMessage());
                }

                try {
                    dispatcher.runSync("addProductToCategory", productToCategoryContext);
                } catch (GenericServiceException e){
                    System.err.println(e.getMessage());
                }
            }
        }


        return success;
    }

    public static Map<String, Object> creatingCatalogAndSupplier(DispatchContext ctx, Map<String, Object> context) throws UnirestException, JsonProcessingException {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Map<String,Object> success = ServiceUtil.returnSuccess();

        GenericValue userLogin = (GenericValue)context.get("userLogin");
        Locale locale = (Locale)context.get("locale");

        //supplier
        Map<String, Object> supplierContext = new HashMap<>(context);
        supplierContext.put("userLogin", userLogin);
        supplierContext.put("locale", locale);

        supplierContext.put("groupName", "BigBuy3");
        supplierContext.put("partyId", "BigBuy3");
        supplierContext.put("partyTypeId", "PARTY_GROUP");
        supplierContext.put("preferredCurrencyUomId", "EUR");

        //supplier role context
        Map<String, Object> supplierRoleContext = new HashMap<>(context);
        supplierRoleContext.put("userLogin", userLogin);
        supplierRoleContext.put("locale", locale);

        supplierRoleContext.put("partyId", "BigBuy3");
        supplierRoleContext.put("roleTypeId", "SUPPLIER");

        try {
            dispatcher.runSync("createPartyGroup", supplierContext);
        } catch (GenericServiceException e){
            System.err.println(e.getMessage());
        }

        try {
            dispatcher.runSync("createPartyRole", supplierRoleContext);
        } catch (GenericServiceException e){
            System.err.println(e.getMessage());
        }

        //catalog

        Map<String, Object> contextCopyCatalog = new HashMap<>(context);
        contextCopyCatalog.put("userLogin", userLogin);
        contextCopyCatalog.put("locale", locale);
        contextCopyCatalog.put("catalogName", "lol");
        contextCopyCatalog.put("prodCatalogId", "lol");

        try {
            dispatcher.runSync("createProdCatalog", contextCopyCatalog);
        } catch (GenericServiceException e){
            System.err.println(e.getMessage());
        }
        return success;
    }


}

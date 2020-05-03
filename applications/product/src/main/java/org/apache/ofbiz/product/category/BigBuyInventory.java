package org.apache.ofbiz.product.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.util.*;

public class BigBuyInventory {
    public static Map<String, Object> addBigBuyCategory(DispatchContext ctx, Map<String, Object> context) throws UnirestException, JsonProcessingException {
        Map<String, Object> success = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String productCategoryTypeId = (String) context.get("productCategoryTypeId");

        //category to catalog context
        Map<String, Object> contextCopyCategoryToCatalog = new HashMap<>(context);
        contextCopyCategoryToCatalog.put("userLogin", userLogin);
        contextCopyCategoryToCatalog.put("locale", locale);
        contextCopyCategoryToCatalog.put("prodCatalogCategoryTypeId", "PCCT_PURCH_ALLW");
        contextCopyCategoryToCatalog.put("prodCatalogId", "BigBuyCatalog");
        contextCopyCategoryToCatalog.put("productCategoryId", productCategoryTypeId);

        Map<String, Object> contextSupplierToCategory = new HashMap<>(context);
        contextSupplierToCategory.put("userLogin", userLogin);
        contextSupplierToCategory.put("locale", locale);
        contextSupplierToCategory.put("partyId", "BigBuy");
        contextSupplierToCategory.put("productCategoryId", productCategoryTypeId);
        contextSupplierToCategory.put("roleTypeId", "SUPPLIER");


        //  Adding Category
        Unirest.setTimeouts(0, 0);
        com.mashape.unirest.http.JsonNode response = Unirest.get("https://api.sandbox.bigbuy.eu/rest/catalog/category/" + productCategoryTypeId + ".json?isoCode=en")
                .header("Authorization", "Bearer YTUwOGI5ZGY5ZDMzNGM4Mjk3ZTY4N2ExODJjYmJiN2VjZDU0ZThiM2Y2NTZkMTlhMjE4NzQ0ZTE4YjgwYjBjNA")
                .asJson().getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(String.valueOf(response));
        if (jsonNode.get("name") != null) {
            context.put("categoryName", String.valueOf(jsonNode.get("name")).replace("\"", ""));
        }
        context.put("productCategoryId", productCategoryTypeId);
        context.put("productCategoryTypeId", "CATALOG_CATEGORY");

        try {
            dispatcher.runSync("createProductCategory", context);
        } catch (GenericServiceException e) {
            System.err.println(e.getMessage());
        }

        //  Adding supplier to category
        try {
            dispatcher.runSync("addPartyToCategory", contextSupplierToCategory);
        } catch (GenericServiceException e) {
            System.err.println(e.getMessage());
        }

        //  Adding Category to Catalog
        try {
            dispatcher.runSync("addProductCategoryToProdCatalog", contextCopyCategoryToCatalog);
        } catch (GenericServiceException e) {
            System.err.println(e.getMessage());
        }

        return success;
    }

    public static Map<String, Object> addBigBuyCatalogAndSupplier(DispatchContext ctx, Map<String, Object> context) throws UnirestException, JsonProcessingException {
        Map<String, Object> success = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        //supplier name
        String partyId = "BigBuy";
        //catalog name
        String catalogNameAndId = "BigBuyCatalog";

        //  Adding Catalog
        Map<String, Object> contextCopyCatalog = new HashMap<>(context);
        contextCopyCatalog.put("userLogin", userLogin);
        contextCopyCatalog.put("locale", locale);
        contextCopyCatalog.put("catalogName", catalogNameAndId);
        contextCopyCatalog.put("prodCatalogId", catalogNameAndId);

        try {
            dispatcher.runSync("createProdCatalog", contextCopyCatalog);
        } catch (GenericServiceException e) {
            System.err.println(e.getMessage());
        }

        //supplier context
        Map<String, Object> supplierContext = new HashMap<>(context);
        supplierContext.put("userLogin", userLogin);
        supplierContext.put("locale", locale);
        supplierContext.put("groupName", partyId);
        supplierContext.put("partyId", partyId);
        supplierContext.put("partyTypeId", "PARTY_GROUP");
        supplierContext.put("preferredCurrencyUomId", "EUR");

        //supplier role context
        Map<String, Object> supplierRoleContext = new HashMap<>(context);
        supplierRoleContext.put("userLogin", userLogin);
        supplierRoleContext.put("locale", locale);
        supplierRoleContext.put("partyId", partyId);
        supplierRoleContext.put("roleTypeId", "SUPPLIER");

        try {
            dispatcher.runSync("createPartyGroup", supplierContext);
        } catch (GenericServiceException e) {
            System.err.println(e.getMessage());
        }

        try {
            dispatcher.runSync("createPartyRole", supplierRoleContext);
        } catch (GenericServiceException e) {
            System.err.println(e.getMessage());
        }

        //supplier to category context
        Map<String, Object> supplierToCatalogContext = new HashMap<>(context);
        supplierToCatalogContext.put("userLogin", userLogin);
        supplierToCatalogContext.put("locale", locale);
        supplierToCatalogContext.put("partyId", partyId);
        supplierToCatalogContext.put("prodCatalogId", catalogNameAndId);
        supplierToCatalogContext.put("roleTypeId", "SUPPLIER");

        try {
            dispatcher.runSync("addProdCatalogToParty", supplierToCatalogContext);
        } catch (GenericServiceException e) {
            System.err.println(e.getMessage());
        }

        return success;
    }

    public static Map<String, Object> addProducts(DispatchContext ctx, Map<String, Object> context) throws UnirestException, JsonProcessingException {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Map<String,Object> success = ServiceUtil.returnSuccess();

        GenericValue userLogin = (GenericValue)context.get("userLogin");
        Locale locale = (Locale)context.get("locale");
        String categoryId = (String) context.get("categoryId");

        ObjectMapper objectMapper = new ObjectMapper();

        Unirest.setTimeouts(0, 0);
        com.mashape.unirest.http.JsonNode response = Unirest.get("https://api.sandbox.bigbuy.eu/rest/catalog/products.json?isoCode=en")
                .header("Authorization", "Bearer YTUwOGI5ZGY5ZDMzNGM4Mjk3ZTY4N2ExODJjYmJiN2VjZDU0ZThiM2Y2NTZkMTlhMjE4NzQ0ZTE4YjgwYjBjNA")
                .asJson().getBody();

        //add product context
        Map<String, String> idPrice = new HashMap<>();

        com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(String.valueOf(response));
        for (JsonNode node : jsonNode) {
            String category  = String.valueOf(node.get("category"));
            String id  = String.valueOf(node.get("id"));
            String price  = String.valueOf(node.get("inShopsPrice"));
            if (category.equals(categoryId)) {
                idPrice.put(id, price);
            }
        }

        Unirest.setTimeouts(0, 0);
        com.mashape.unirest.http.JsonNode nameList = Unirest.get("https://api.sandbox.bigbuy.eu/rest/catalog/productsinformation.json?isoCode=en")
                .header("Authorization", "Bearer YTUwOGI5ZGY5ZDMzNGM4Mjk3ZTY4N2ExODJjYmJiN2VjZDU0ZThiM2Y2NTZkMTlhMjE4NzQ0ZTE4YjgwYjBjNA")
                .asJson().getBody();

        Set<String> idList = idPrice.keySet();
        com.fasterxml.jackson.databind.JsonNode jsonNode2 = objectMapper.readTree(String.valueOf(nameList));
        for (JsonNode node : jsonNode2) {
            String id  = String.valueOf(node.get("id"));
            if (idList.contains(id)) {
                //createProduct
                Map<String, Object> productContext = new HashMap<>(context);
                productContext.put("userLogin", userLogin);
                productContext.put("locale", locale);
                String name = String.valueOf(node.get("name")).replace("\"", "");
                productContext.put("productName", name);
                productContext.put("internalName", name);
                productContext.put("productId", id);
                productContext.put("productTypeId", "GOOD");

                try {
                    dispatcher.runSync("createProduct", productContext);
                } catch (GenericServiceException e){
                    System.err.println(e.getMessage());
                }

                //add a price
                double priceDB = Double.parseDouble(idPrice.get(id));
                BigDecimal priceBD = BigDecimal.valueOf(priceDB);
                Map<String, Object> priceContext = new HashMap<>(context);
                priceContext.put("userLogin", userLogin);
                priceContext.put("locale", locale);
                priceContext.put("cost", priceBD);
                priceContext.put("productId", id);
                priceContext.put("costUomId", "EUR");

                try {
                    dispatcher.runSync("createCostComponent", priceContext);
                } catch (GenericServiceException e){
                    System.err.println(e.getMessage());
                }

                //add supplier to product context
                Map<String, Object> partyToProductContext = new HashMap<>(context);
                partyToProductContext.put("userLogin", userLogin);
                partyToProductContext.put("locale", locale);

                partyToProductContext.put("partyId", "BigBuy");
                partyToProductContext.put("productId", id);
                partyToProductContext.put("roleTypeId", "SUPPLIER");

                try {
                    dispatcher.runSync("addPartyToProduct", partyToProductContext);
                } catch (GenericServiceException e){
                    System.err.println(e.getMessage());
                }

                //add product to category context

                Map<String, Object> productToCategoryContext = new HashMap<>(context);
                productToCategoryContext.put("userLogin", userLogin);
                productToCategoryContext.put("locale", locale);

                productToCategoryContext.put("productCategoryId", categoryId);
                productToCategoryContext.put("productId", id);

                try {
                    dispatcher.runSync("addProductToCategory", productToCategoryContext);
                } catch (GenericServiceException e){
                    System.err.println(e.getMessage());
                }
            }
        }


        return success;
    }
}
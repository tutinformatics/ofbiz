package org.apache.ofbiz.entity.nosql;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;

import java.net.MalformedURLException;
import java.net.URL;

public class CouchDbConnector {

    private static CloudantClient client;

    static {
        try {
            client = ClientBuilder.url(new URL("http://127.0.0.1:5984"))
                          .username("ofbiz")
                          .password("ofbiz")
                          .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void createDb(String name) {
        client.database(name, true);
    }

    public static void deleteDb(String name) {
        client.deleteDB(name);
    }

    public static void something() {
        Database db = client.database("hell", true);
//        db.
    }
}

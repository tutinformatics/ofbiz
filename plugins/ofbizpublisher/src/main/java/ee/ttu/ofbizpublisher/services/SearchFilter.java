package ee.ttu.ofbizpublisher.services;

import java.util.List;

public class SearchFilter {

    List<Filter> filterParameters;
    String entityName;

    public SearchFilter(List<Filter> filterParameters, String entityName) {
        this.filterParameters = filterParameters;
        this.entityName = entityName;
    }
}

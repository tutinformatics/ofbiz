package ee.ttu.ofbizpublisher.services;

public class Filter {

    String fieldName;
    String operation;
    String value;

    public Filter(String fieldName, String operation, String value) {
        this.fieldName = fieldName;
        this.operation = operation;
        this.value = value;
    }
}

package ee.taltech.marketing.affiliate.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class AffiliateDTO {
    String firstName;
    String lastName;
    Timestamp date = null;
    String email;

    public enum Status {
        ACTIVE,
        NOT_APPROVED
    }

    Status status;
}

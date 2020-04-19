package ee.taltech.marketing.affiliate.model;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class AffiliateDTO {
    String firstName;
    String lastName;
    Timestamp date = null;
    List<AffiliateDTO> subAffiliates;
    String email;

    public enum Status {
        ACTIVE,
        PENDING,
        DECLINED
    }

    Status status;
}

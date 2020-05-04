package org.apache.ofbiz.timesheet.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class TimesheetCmd {

    private String partyId;

    private String clientPartyId;

    private String statusId;

    private String approvedByUserLoginId;

}

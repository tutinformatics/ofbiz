package org.apache.ofbiz.project.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ProjectTaskCmd {

    private String workEffortName;

    private String workEffortTypeId;

    private String statusId;

    private String partyId;

    private String workEffortParentId;

    private String currentStatusId;

    private String roleTypeId;

    private Integer estimatedHours;

    private Integer priority;

    private String description;

    //TODO: check how service consumes dates
    //private LocalDate estimatedStartDate;

    //private LocalDate estimatedCompletionDate;
}

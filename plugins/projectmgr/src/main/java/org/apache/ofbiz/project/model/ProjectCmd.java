package org.apache.ofbiz.project.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ProjectCmd {

    private String workEffortName;

    private String workEffortTypeId = "PROJECT";

    private String currentStatusId = "_NA_";

}

package com.taltech.workspace.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class WorkspaceCmd {

    private String title;

    private String url;

    private String userId;

}

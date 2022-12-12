package com.facets.cloud.assignment.request;

import javax.validation.constraints.NotNull;
import java.util.List;

public class AssignVirtualChildNodesRequest {

    @NotNull
    public String parentVirtualNodeName;

    //Can be empty but not null
    public List<@NotNull String> virtualNodeNameList;
}

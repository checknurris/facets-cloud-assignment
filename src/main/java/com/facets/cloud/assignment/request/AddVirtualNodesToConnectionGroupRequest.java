package com.facets.cloud.assignment.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class AddVirtualNodesToConnectionGroupRequest {
    @NotEmpty
    public List<@NotNull AssignVirtualChildNodesRequest> virtualChildNodesRequestList;
}

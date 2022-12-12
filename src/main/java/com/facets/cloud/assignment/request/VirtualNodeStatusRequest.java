package com.facets.cloud.assignment.request;

import com.facets.cloud.assignment.Util.VirtualNodeStatus;

import javax.validation.constraints.NotNull;

public class VirtualNodeStatusRequest {

    @NotNull
    public VirtualNodeStatus virtualNodeStatus;
}

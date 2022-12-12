package com.facets.cloud.assignment.request.connectionGroup;

import com.facets.cloud.assignment.Util.ConnectionGroupStatus;

import javax.validation.constraints.NotNull;

public class ConnectionGroupStatusRequest {

    @NotNull
    public ConnectionGroupStatus connectionGroupStatus;

}

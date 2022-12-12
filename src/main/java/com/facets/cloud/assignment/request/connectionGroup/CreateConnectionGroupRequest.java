package com.facets.cloud.assignment.request.connectionGroup;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CreateConnectionGroupRequest {
    @NotNull
    @Size(min = 3, max = 14)
    @Pattern(regexp = "[\\w]*", message = "No spaces or special characters allowed in connection group name.")
    public String connectionGroupName;
}

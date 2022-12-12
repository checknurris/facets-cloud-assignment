package com.facets.cloud.assignment.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CreateVirtualNodeRequest {
    @NotNull
    @Size(min = 3, max = 14)
    @Pattern(regexp = "[\\w]*", message = "No spaces or special characters allowed in node name.")
    public String nodeName;
}

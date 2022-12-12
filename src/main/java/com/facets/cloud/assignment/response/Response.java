package com.facets.cloud.assignment.response;

import com.facets.cloud.assignment.Util.FacetsCloudUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class Response {

    public static ResponseEntity returnSuccessResponse(Object response) throws JsonProcessingException {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(FacetsCloudUtils.objectMapper().writeValueAsString(response));
    }
    public static ResponseEntity returnError(Object response) throws JsonProcessingException {
        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(FacetsCloudUtils.objectMapper().writeValueAsString(response));
    }
}

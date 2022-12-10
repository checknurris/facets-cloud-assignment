package com.facets.cloud.assignment.Util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.NoArgsConstructor;

import java.util.TimeZone;


@NoArgsConstructor
public final class FacetsCloudUtils {
     public static ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

        return mapper;
    }
}

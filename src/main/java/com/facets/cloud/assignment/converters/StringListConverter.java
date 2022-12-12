package com.facets.cloud.assignment.converters;


import com.facets.cloud.assignment.Util.FacetsCloudUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class StringListConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
        try{
            return FacetsCloudUtils.objectMapper().writeValueAsString(stringList);
        }catch (JsonProcessingException e){
            String errorMessage = "Exception occurred while converting stringList to db column.";
            log.error("Unexpected exception occurred: " + errorMessage);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if(dbData == null || dbData.equals("{}") || dbData.equals("\"[]\"") || dbData.equals("\"null\"")){
            return new ArrayList<>();
        }
        try{
            return FacetsCloudUtils.objectMapper().readValue(dbData, new TypeReference<>() {});
        }catch (Exception e){
            String errorMessage = "Exception occurred while converting db column to object.";
            log.error("Unexpected exception occurred: " + errorMessage);
            throw new RuntimeException(e);
        }
    }
}

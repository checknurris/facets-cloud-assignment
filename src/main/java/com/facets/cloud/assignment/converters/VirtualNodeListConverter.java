package com.facets.cloud.assignment.converters;

import com.facets.cloud.assignment.domains.VirtualNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.facets.cloud.assignment.Util.FacetsCloudUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import java.util.List;

@Slf4j
public class VirtualNodeListConverter implements AttributeConverter<List<VirtualNode>, String> {
    @Override
    public String convertToDatabaseColumn(List<VirtualNode> virtualNodeList) {
        try{
            return FacetsCloudUtils.objectMapper().writeValueAsString(virtualNodeList);
        }catch (JsonProcessingException e){
            String errorMessage = "Exception occurred while converting virtualNodeList to db column.";
            log.error("Unexpected exception occurred: " + errorMessage);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<VirtualNode> convertToEntityAttribute(String dbData) {
        if(dbData == null || dbData.equals("{}")){
            return null;
        }
        try{
            return FacetsCloudUtils.objectMapper().readValue(dbData, new TypeReference<List<VirtualNode>>(){});
        }catch (Exception e){
            String errorMessage = "Exception occurred while converting db column to object.";
            log.error("Unexpected exception occurred: " + errorMessage);
            throw new RuntimeException(e);
        }
    }
}

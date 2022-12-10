package com.facets.cloud.assignment.controller;

import com.facets.cloud.assignment.Util.FacetsCloudUtils;
import com.facets.cloud.assignment.domains.VirtualNode;
import com.facets.cloud.assignment.repository.VirtualNodeRepository;
import com.facets.cloud.assignment.request.CreateVirtualNodeRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "virtualNode")
@Slf4j
public class VirtualNodeController {

    @Autowired
    VirtualNodeRepository virtualNodeRepository;

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createVirtualNode(@RequestBody @Valid CreateVirtualNodeRequest request) throws JsonProcessingException {

        VirtualNode virtualNode = virtualNodeRepository.findByNodeName(request.nodeName);

        if(virtualNode != null){
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("Duplicate node name");
        }

        VirtualNode newVirtualNode = new VirtualNode();
        newVirtualNode.setNodeName(request.nodeName);
        virtualNodeRepository.save(newVirtualNode);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(FacetsCloudUtils.objectMapper().writeValueAsString(newVirtualNode));
    }

}

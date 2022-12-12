package com.facets.cloud.assignment.service;

import com.facets.cloud.assignment.Util.ConnectionGroupStatus;
import com.facets.cloud.assignment.Util.FacetsCloudUtils;
import com.facets.cloud.assignment.Util.VirtualNodeStatus;
import com.facets.cloud.assignment.domains.ConnectionGroup;
import com.facets.cloud.assignment.domains.VirtualNode;
import com.facets.cloud.assignment.repository.ConnectionGroupRepository;
import com.facets.cloud.assignment.repository.VirtualNodeRepository;
import com.facets.cloud.assignment.request.CreateVirtualNodeRequest;
import com.facets.cloud.assignment.response.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
@Service
@Slf4j
public class VirtualNodeService {

    @Autowired
    VirtualNodeRepository virtualNodeRepository;

    @Autowired
    ConnectionGroupRepository connectionGroupRepository;

    public Either<ErrorResponse, VirtualNode> createVirtualNode(CreateVirtualNodeRequest request) {
        VirtualNode virtualNode = virtualNodeRepository.findByNodeName(request.nodeName);

        if (virtualNode != null) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setReason("Duplicate node creation request with node name: " + request.nodeName);
            log.error(errorResponse.getReason());
            return Either.left(errorResponse);
        }

        VirtualNode newVirtualNode = new VirtualNode();
        newVirtualNode.setNodeName(request.nodeName);

        return Either.right(virtualNodeRepository.save(newVirtualNode));
    }

    public Either<ErrorResponse, VirtualNode> getVirtualNode(String nodeName) {
        VirtualNode virtualNode = virtualNodeRepository.findByNodeName(nodeName);
        if (virtualNode == null) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setReason("Invalid node name " + nodeName);
            log.error(errorResponse.getReason());
            return Either.left(errorResponse);
        }

        return Either.right(virtualNode);
    }

    public Either<ErrorResponse, ConnectionGroup> getConnectionGroup(String nodeName) {
        VirtualNode virtualNode = virtualNodeRepository.findByNodeName(nodeName);
        if (virtualNode == null) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setReason("Invalid node name " + nodeName);
            log.error(errorResponse.getReason());
            return Either.left(errorResponse);
        }

        return Either.right(virtualNode.getConnectionGroup());
    }

    public Either<ErrorResponse, VirtualNode> setVirtualNodeStatus(String nodeName, VirtualNodeStatus virtualNodeStatus) {
        VirtualNode virtualNode = virtualNodeRepository.findByNodeName(nodeName);
        if (virtualNode == null) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setReason("Invalid node name " + nodeName);
            log.error(errorResponse.getReason());
            return Either.left(errorResponse);
        }

        virtualNode.setStatus(virtualNodeStatus);

        return Either.right(virtualNodeRepository.save(virtualNode));
    }

    @Async
    public void asyncSetNodeStatus(String nodeName, VirtualNodeStatus virtualNodeStatus) {
        VirtualNode virtualNode = virtualNodeRepository.findByNodeName(nodeName);
        if (virtualNode == null) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setReason("Invalid node name " + nodeName);
            log.error(errorResponse.getReason());
        }

        virtualNode.setStatus(virtualNodeStatus);

    }

    public Either<ErrorResponse, VirtualNode> validateAndAddChildNode(ConnectionGroup connectionGroup, VirtualNode parentVirtualNode, String childNodeName) throws JsonProcessingException {
        VirtualNode childNode = virtualNodeRepository.findByNodeName(childNodeName);
        if (childNode == null) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setReason("Invalid node name " + childNodeName + ". Aborting operation.");
            log.error(errorResponse.getReason());
            return Either.left(errorResponse);
        }

        if(childNode == parentVirtualNode){
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setReason("Cannot define heirarchy to same node.");
            log.error(errorResponse.getReason());
            return Either.left(errorResponse);
        }

        if (childNode.getConnectionGroup() != null && childNode.getConnectionGroup() != connectionGroup) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setReason("Virtual nodes do not belong to the same connection group. Aborting operation.");
            log.error(errorResponse.getReason());
            return Either.left(errorResponse);
        }

        childNode.setConnectionGroup(connectionGroup);

        if (parentVirtualNode.getIsPrimaryNode()) {
            if(!childNode.getParentVirtualNodeList().contains(parentVirtualNode.getNodeName())){
                childNode.getParentVirtualNodeList().add(parentVirtualNode.getNodeName());
            }
            if(!parentVirtualNode.getChildVirtualNodeList().contains(childNodeName)){
                parentVirtualNode.getChildVirtualNodeList().add(childNode.getNodeName());
            }
            log.info("child: " + childNode.getNodeName() + " : "  +FacetsCloudUtils.objectMapper().writeValueAsString(childNode.getParentVirtualNodeList()));

            log.info("parent: " + parentVirtualNode.getNodeName() + " : "  + FacetsCloudUtils.objectMapper().writeValueAsString(parentVirtualNode.getChildVirtualNodeList()));
        } else if (!parentVirtualNode.getIsPrimaryNode() && parentVirtualNode.getParentVirtualNodeList().isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setReason("Cannot have orphaned nodes out of hierarchy in same connection group. " +
                    "Error at adding: " + childNode.getNodeName() + " to parent node: " + parentVirtualNode.getNodeName());
            log.error(errorResponse.getReason());
            return Either.left(errorResponse);
        } else if (!parentVirtualNode.getIsPrimaryNode() && parentVirtualNode.getParentVirtualNodeList().contains(childNode.getNodeName())) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setReason("Cannot have cyclic hierarchy definition for parents." +
                    "Error at adding: " + childNode.getNodeName() + " to parent node: " + parentVirtualNode.getNodeName());
            log.error(errorResponse.getReason());
            return Either.left(errorResponse);
        } else if (!parentVirtualNode.getIsPrimaryNode() && !childNode.getParentVirtualNodeList().isEmpty() && parentVirtualNode.getParentVirtualNodeList().get(0).equals(childNode.getParentVirtualNodeList().get(0))) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setReason("Cannot have cyclic hierarchy defined for leaf nodes." +
                    "Error at adding: " + childNode.getNodeName() + " to parent node: " + parentVirtualNode.getNodeName());
            log.error(errorResponse.getReason());
            return Either.left(errorResponse);
        } else if(!parentVirtualNode.getIsPrimaryNode()){
            for (String parentNodeName : parentVirtualNode.getParentVirtualNodeList()) {
                if(!childNode.getParentVirtualNodeList().contains(parentNodeName)) {
                    childNode.getParentVirtualNodeList().add(parentNodeName);
                }
            }
            if(!childNode.getParentVirtualNodeList().contains(parentVirtualNode.getNodeName())){
                childNode.getParentVirtualNodeList().add(parentVirtualNode.getNodeName());
            }
            if(!parentVirtualNode.getChildVirtualNodeList().contains(childNodeName)){
                parentVirtualNode.getChildVirtualNodeList().add(childNode.getNodeName());
            }
        }

        virtualNodeRepository.save(childNode);
        virtualNodeRepository.save(parentVirtualNode);
        return Either.right(parentVirtualNode);
    }

}

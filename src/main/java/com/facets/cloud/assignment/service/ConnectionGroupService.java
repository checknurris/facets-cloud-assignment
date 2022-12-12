package com.facets.cloud.assignment.service;

import com.facets.cloud.assignment.Util.ConnectionGroupStatus;
import com.facets.cloud.assignment.Util.VirtualNodeStatus;
import com.facets.cloud.assignment.domains.ConnectionGroup;
import com.facets.cloud.assignment.domains.VirtualNode;
import com.facets.cloud.assignment.repository.ConnectionGroupRepository;
import com.facets.cloud.assignment.repository.VirtualNodeRepository;
import com.facets.cloud.assignment.request.AssignVirtualChildNodesRequest;
import com.facets.cloud.assignment.request.connectionGroup.CreateConnectionGroupRequest;
import com.facets.cloud.assignment.response.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service
@Slf4j
public class ConnectionGroupService {

    @Autowired
    ConnectionGroupRepository connectionGroupRepository;

    @Autowired
    VirtualNodeService virtualNodeService;

    @Autowired
    VirtualNodeRepository virtualNodeRepository;

    public Either<ErrorResponse, ConnectionGroup> createConnectionGroup(CreateConnectionGroupRequest request) {
        ConnectionGroup connectionGroup = connectionGroupRepository.findByConnectionGroupName(request.connectionGroupName);

        if (connectionGroup != null) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setReason("Duplicate connection group creation request with node name: " + request.connectionGroupName);
            log.error(errorResponse.getReason());
            return Either.left(errorResponse);
        }

        connectionGroup = new ConnectionGroup();
        connectionGroup.setConnectionGroupName(request.connectionGroupName);

        return Either.right(connectionGroupRepository.save(connectionGroup));
    }

    public Either<ErrorResponse, ConnectionGroup> getConnectionGroup(String connectionGroupName) {
        ConnectionGroup connectionGroup = connectionGroupRepository.findByConnectionGroupName(connectionGroupName);
        if (connectionGroup == null) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setReason("Connection Group name " + connectionGroupName + " not found.");
            log.error(errorResponse.getReason());
            return Either.left(errorResponse);
        }

        return Either.right(connectionGroup);
    }

    public Either<ErrorResponse, ConnectionGroup> setConnectionGroupStatus(String connectionGroupName, ConnectionGroupStatus groupStatus) {
        ErrorResponse errorResponse = new ErrorResponse();

        ConnectionGroup connectionGroup = connectionGroupRepository.findByConnectionGroupName(connectionGroupName);
        if (connectionGroup == null) {
            errorResponse.setReason("Connection Group name " + connectionGroupName + " not found.");
            log.error(errorResponse.getReason());
            return Either.left(errorResponse);
        }

        if (connectionGroup.getVirtualNodeList().isEmpty()) {
            errorResponse.setReason("Connection Group: " + connectionGroupName + " does not have any nodes.");
            log.error(errorResponse.getReason());
            return Either.left(errorResponse);
        }

        switch (groupStatus) {
            case ONLINE:
                connectionGroup.setStatus(ConnectionGroupStatus.ONLINE);
                break;
            case OFFLINE:
                connectionGroup.setStatus(ConnectionGroupStatus.OFFLINE);
                for (VirtualNode virtualNode : connectionGroup.getVirtualNodeList()) {
                    virtualNodeService.asyncSetNodeStatus(virtualNode.getNodeName(), VirtualNodeStatus.OFFLINE);
                }
                break;
            case OUT_OF_SERVICE:
                connectionGroup.setStatus(ConnectionGroupStatus.OUT_OF_SERVICE);
                for (VirtualNode virtualNode : connectionGroup.getVirtualNodeList()) {
                    virtualNodeService.asyncSetNodeStatus(virtualNode.getNodeName(), VirtualNodeStatus.OUT_OF_SERVICE);
                }
                break;
            default:
                errorResponse.setReason("Invalid status update: " + groupStatus + " for connection group: " + connectionGroupName);
                log.error(errorResponse.getReason());
                return Either.left(errorResponse);
        }

        return Either.right(connectionGroupRepository.save(connectionGroup));
    }

    public Either<ErrorResponse, ConnectionGroup> setAllVirtualNodesStatus(String connectionGroupName, VirtualNodeStatus virtualNodeStatus) {
        ErrorResponse errorResponse = new ErrorResponse();

        ConnectionGroup connectionGroup = connectionGroupRepository.findByConnectionGroupName(connectionGroupName);
        if (connectionGroup == null) {
            errorResponse.setReason("Connection Group name " + connectionGroupName + " not found.");
            log.error(errorResponse.getReason());
            return Either.left(errorResponse);
        }

        if (connectionGroup.getVirtualNodeList().isEmpty()) {
            errorResponse.setReason("Connection Group: " + connectionGroupName + " does not have any nodes.");
            log.error(errorResponse.getReason());
            return Either.left(errorResponse);
        }

        switch (virtualNodeStatus) {
            case ONLINE:
                if (connectionGroup.getStatus() != ConnectionGroupStatus.ONLINE) {
                    errorResponse.setReason("Connection Group name " + connectionGroupName + " is not online. Cannot set nodes online.");
                    log.error(errorResponse.getReason());
                    return Either.left(errorResponse);
                }
        }

        for (VirtualNode virtualNode : connectionGroup.getVirtualNodeList()) {
            virtualNodeService.asyncSetNodeStatus(virtualNode.getNodeName(), virtualNodeStatus);
        }

        return Either.right(connectionGroup);
    }


    public Either<ErrorResponse, ConnectionGroup> addVirtualNodes(String connectionGroupName, AssignVirtualChildNodesRequest[] request) {
        ErrorResponse errorResponse = new ErrorResponse();
        try {
            ConnectionGroup connectionGroup = connectionGroupRepository.findByConnectionGroupName(connectionGroupName);
            if (connectionGroup == null) {
                errorResponse.setReason("Connection Group name " + connectionGroupName + " not found.");
                log.error(errorResponse.getReason());
                return Either.left(errorResponse);
            }

            for (AssignVirtualChildNodesRequest assignVirtualChildNodesRequest : request) {
                connectionGroup = validateAndAddVirtualNodes(connectionGroup, assignVirtualChildNodesRequest);
            }

            return Either.right(connectionGroup);
        }catch (Exception e){
            errorResponse.setReason(e.getMessage());
            log.error("Exception occurred: " + e);
            return Either.left(errorResponse);
        }
    }

    @Transactional
    ConnectionGroup validateAndAddVirtualNodes(ConnectionGroup connectionGroup, AssignVirtualChildNodesRequest request) throws JsonProcessingException {
        ErrorResponse errorResponse = new ErrorResponse();

        VirtualNode virtualNode = virtualNodeRepository.findByNodeName(request.parentVirtualNodeName);
        if (virtualNode == null) {
            errorResponse.setReason("Invalid node name " + request.parentVirtualNodeName + ". Aborting operation.");
            log.error(errorResponse.getReason());
            throw new UnsupportedOperationException(errorResponse.getReason());
        }

        if (virtualNode.getConnectionGroup() != null && virtualNode.getConnectionGroup() != connectionGroup) {
            errorResponse.setReason("Node belongs to another connection group " + request.parentVirtualNodeName + ". Aborting operation.");
            log.error(errorResponse.getReason());
            throw new UnsupportedOperationException(errorResponse.getReason());
        }

        if (connectionGroup.getVirtualNodeList().isEmpty()) {
            log.info("Setting node as primary.");
            virtualNode.setIsPrimaryNode(true);
        } else if (!virtualNode.getIsPrimaryNode() && request.virtualNodeNameList == null) {
            errorResponse.setReason("Cannot have orphaned nodes out of hierarchy in same connection group. " +
                    "Error adding: " + request.parentVirtualNodeName);
            log.error(errorResponse.getReason());
            throw new UnsupportedOperationException(errorResponse.getReason());
        }

        virtualNode.setConnectionGroup(connectionGroup);
        if (request.virtualNodeNameList != null) {
            for (String childNodeName : request.virtualNodeNameList) {
                Either<ErrorResponse, VirtualNode> virtualNodeEither = virtualNodeService.validateAndAddChildNode(connectionGroup, virtualNode, childNodeName);
                if (virtualNodeEither.isLeft()) {
                    log.error(virtualNodeEither.getLeft().getReason());
                    throw new UnsupportedOperationException(virtualNodeEither.getLeft().getReason());
                }
            }
        }

        return connectionGroupRepository.save(connectionGroup);
    }

}

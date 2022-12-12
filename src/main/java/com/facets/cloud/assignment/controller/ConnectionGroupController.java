package com.facets.cloud.assignment.controller;

import com.facets.cloud.assignment.Util.FacetsCloudUtils;
import com.facets.cloud.assignment.domains.ConnectionGroup;
import com.facets.cloud.assignment.request.AssignVirtualChildNodesRequest;
import com.facets.cloud.assignment.request.VirtualNodeStatusRequest;
import com.facets.cloud.assignment.request.connectionGroup.ConnectionGroupStatusRequest;
import com.facets.cloud.assignment.request.connectionGroup.CreateConnectionGroupRequest;
import com.facets.cloud.assignment.response.ErrorResponse;
import com.facets.cloud.assignment.response.Response;
import com.facets.cloud.assignment.service.ConnectionGroupService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.vavr.control.Either;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "connectionGroup")
@Slf4j
public class ConnectionGroupController {

    @Autowired
    ConnectionGroupService connectionGroupService;

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createVirtualNode(@RequestBody @Valid CreateConnectionGroupRequest request) throws JsonProcessingException {

        Either<ErrorResponse, ConnectionGroup> connectionGroup = connectionGroupService.createConnectionGroup(request);
        if (connectionGroup.isLeft()) {
            return Response.returnError(connectionGroup.getLeft());
        }

        return Response.returnSuccessResponse(connectionGroup.get());
    }

    @GetMapping(value = "/{connectionGroupName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createVirtualNode(@PathVariable(name = "connectionGroupName") String connectionGroupName) throws JsonProcessingException {

        Either<ErrorResponse, ConnectionGroup> connectionGroup = connectionGroupService.getConnectionGroup(connectionGroupName);
        if (connectionGroup.isLeft()) {
            return Response.returnError(connectionGroup.getLeft());
        }

        return Response.returnSuccessResponse(connectionGroup.get());
    }

    @SneakyThrows
    @PutMapping(value = "/{connectionGroupName}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setNodeStatus(@PathVariable(name = "connectionGroupName") String connectionGroupName,
                                        @RequestBody @Valid ConnectionGroupStatusRequest request) {
        Either<ErrorResponse, ConnectionGroup> connectionGroupEither = connectionGroupService.setConnectionGroupStatus(connectionGroupName, request.connectionGroupStatus);
        if (connectionGroupEither.isLeft()) {
            return Response.returnError(connectionGroupEither.getLeft());
        }

        return Response.returnSuccessResponse(connectionGroupEither.get());
    }

    @SneakyThrows
    @PutMapping(value = "/{connectionGroupName}/allVirtualNodesStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setAllVirtualNodesStatus(@PathVariable(name = "connectionGroupName") String connectionGroupName,
                                                   @RequestBody @Valid VirtualNodeStatusRequest request) {
        Either<ErrorResponse, ConnectionGroup> connectionGroupEither = connectionGroupService.setAllVirtualNodesStatus(connectionGroupName, request.virtualNodeStatus);
        if (connectionGroupEither.isLeft()) {
            return Response.returnError(connectionGroupEither.getLeft());
        }

        return Response.returnSuccessResponse(connectionGroupEither.get());
    }

    @SneakyThrows
    @PostMapping(value = "/{connectionGroupName}/virtualNodes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addVirtualNodes(@PathVariable(name = "connectionGroupName") String connectionGroupName,
                                          @RequestBody String request) {
        Either<ErrorResponse, ConnectionGroup> connectionGroupEither = null;
        AssignVirtualChildNodesRequest[] requestList = FacetsCloudUtils.objectMapper().readValue(request, AssignVirtualChildNodesRequest[].class);
        connectionGroupEither = connectionGroupService.addVirtualNodes(connectionGroupName, requestList);

        if (connectionGroupEither.isLeft()) {
            return Response.returnError(connectionGroupEither.getLeft());
        }

        return Response.returnSuccessResponse("Successfully added nodes.");
    }

}

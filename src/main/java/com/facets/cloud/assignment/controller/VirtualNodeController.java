package com.facets.cloud.assignment.controller;

import com.facets.cloud.assignment.domains.ConnectionGroup;
import com.facets.cloud.assignment.domains.VirtualNode;
import com.facets.cloud.assignment.request.AssignVirtualChildNodesRequest;
import com.facets.cloud.assignment.request.CreateVirtualNodeRequest;
import com.facets.cloud.assignment.request.VirtualNodeStatusRequest;
import com.facets.cloud.assignment.response.ErrorResponse;
import com.facets.cloud.assignment.response.Response;
import com.facets.cloud.assignment.service.VirtualNodeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "virtualNode")
@Slf4j
public class VirtualNodeController {

    @Autowired
    VirtualNodeService virtualNodeService;

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createVirtualNode(@RequestBody @Valid CreateVirtualNodeRequest request) throws JsonProcessingException {

        Either<ErrorResponse, VirtualNode> virtualNodeEither = virtualNodeService.createVirtualNode(request);
        if (virtualNodeEither.isLeft()) {
            return Response.returnError(virtualNodeEither.getLeft());
        }

        return Response.returnSuccessResponse(virtualNodeEither.get());
    }

    @GetMapping(value = "/{nodeName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getVirtualNode(@PathVariable(name = "nodeName") String nodeName) throws JsonProcessingException {
        Either<ErrorResponse, VirtualNode> virtualNodeEither = virtualNodeService.getVirtualNode(nodeName);
        if (virtualNodeEither.isLeft()) {
            return Response.returnError(virtualNodeEither.getLeft());
        }

        return Response.returnSuccessResponse(virtualNodeEither.get());
    }

    /*@PutMapping(value = "/childNodes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity assignVirtualChildNodes(@RequestBody @Valid AssignVirtualChildNodesRequest request) throws JsonProcessingException {
        Either<ErrorResponse, VirtualNode> virtualNodeEither = virtualNodeService.assignChildVirtualNodes(request);
        if (virtualNodeEither.isLeft()) {
            return Response.returnError(virtualNodeEither.getLeft());
        }

        return Response.returnSuccessResponse(virtualNodeEither.get());
    }*/

    @GetMapping(value = "/{nodeName}/connectionGroup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getConnectionGroup(@PathVariable(name = "nodeName") String nodeName,
                                        @RequestBody @Valid VirtualNodeStatusRequest request) throws JsonProcessingException {
        Either<ErrorResponse, ConnectionGroup> virtualNodeEither = virtualNodeService.getConnectionGroup(nodeName);
        if (virtualNodeEither.isLeft()) {
            return Response.returnError(virtualNodeEither.getLeft());
        }

        return Response.returnSuccessResponse(virtualNodeEither.get());
    }

    @PutMapping(value = "/{nodeName}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setNodeStatus(@PathVariable(name = "nodeName") String nodeName,
                                        @RequestBody @Valid VirtualNodeStatusRequest request) throws JsonProcessingException {
        Either<ErrorResponse, VirtualNode> virtualNodeEither = virtualNodeService.setVirtualNodeStatus(nodeName,request.virtualNodeStatus);
        if (virtualNodeEither.isLeft()) {
            return Response.returnError(virtualNodeEither.getLeft());
        }

        return Response.returnSuccessResponse(virtualNodeEither.get());
    }

}

package com.facets.cloud.assignment.repository;

import com.facets.cloud.assignment.domains.VirtualNode;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface VirtualNodeRepository extends CrudRepository<VirtualNode, String> {

    VirtualNode findByNodeName(String nodeName);

}

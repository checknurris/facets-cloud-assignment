package com.facets.cloud.assignment.domains;

import com.facets.cloud.assignment.Util.VirtualNodeStatus;
import com.facets.cloud.assignment.converters.StringListConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class VirtualNode implements Serializable {

    public VirtualNode() {
        this.status = VirtualNodeStatus.OFFLINE;
        this.childVirtualNodeList = new ArrayList<>();
        this.parentVirtualNodeList = new ArrayList<>();
        this.isPrimaryNode = false;
    }

    @Id
    @GenericGenerator(name = "vnode_id", strategy = "com.facets.cloud.assignment.Util.UniqueIdGenerator")
    @GeneratedValue(generator = "vnode_id")
    String id;

    String nodeName;

    @Enumerated(value = EnumType.STRING)
    VirtualNodeStatus status;

    @Convert(converter = StringListConverter.class)
    List<String> parentVirtualNodeList;
    @Convert(converter = StringListConverter.class)
    List<String> childVirtualNodeList;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connection_group_id", referencedColumnName = "id")
    ConnectionGroup connectionGroup;

    Boolean isPrimaryNode;

}

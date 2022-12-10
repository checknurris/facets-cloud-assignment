package com.facets.cloud.assignment.domains;

import com.facets.cloud.assignment.Util.VirtualNodeStatus;
import com.facets.cloud.assignment.converters.VirtualNodeListConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class VirtualNode {
    @Id
    @GenericGenerator(name = "vnode_id", strategy = "com.facets.cloud.assignment.Util.UniqueIdGenerator")
    @GeneratedValue(generator = "vnode_id")
    String id;

    @Enumerated(value = EnumType.STRING)
    VirtualNodeStatus status;

    @Convert(converter = VirtualNodeListConverter.class)
    @Column(columnDefinition = "JSON")
    List<VirtualNode> childVirtualNodeList;

    @ManyToOne
    ConnectionGroup connectionGroup;
}

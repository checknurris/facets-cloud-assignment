package com.facets.cloud.assignment.domains;

import com.facets.cloud.assignment.Util.ConnectionGroupStatus;
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
public class ConnectionGroup {
    @Id
    @GenericGenerator(name = "cg_id", strategy = "com.facets.cloud.assignment.Util.UniqueIdGenerator")
    @GeneratedValue(generator = "cg_id")
    String id;

    @Enumerated(value = EnumType.STRING)
    ConnectionGroupStatus status;

    @OneToMany(mappedBy = "connectionGroup", fetch = FetchType.LAZY)
    List<VirtualNode> virtualNodeList;
}

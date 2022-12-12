package com.facets.cloud.assignment.domains;

import com.facets.cloud.assignment.Util.ConnectionGroupStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class ConnectionGroup implements Serializable {

    public ConnectionGroup() {
        this.status = ConnectionGroupStatus.OFFLINE;
        this.virtualNodeList = new ArrayList<>();
    }

    @Id
    @GenericGenerator(name = "cg_id", strategy = "com.facets.cloud.assignment.Util.UniqueIdGenerator")
    @GeneratedValue(generator = "cg_id")
    String id;

    String connectionGroupName;

    @Enumerated(value = EnumType.STRING)
    ConnectionGroupStatus status;

    @JsonManagedReference
    @OneToMany(mappedBy = "connectionGroup", fetch = FetchType.LAZY)
    List<VirtualNode> virtualNodeList;

}

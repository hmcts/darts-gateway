package com.synapps.moj.dfs.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegisterNodeResponse", propOrder = {
    "nodeId"
})
public class RegisterNodeResponse
    extends DARTSResponse {

    @XmlElement(name = "node_id")
    protected String nodeId;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String value) {
        this.nodeId = value;
    }

}

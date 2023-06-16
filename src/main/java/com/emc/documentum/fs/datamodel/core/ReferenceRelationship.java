package com.emc.documentum.fs.datamodel.core;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferenceRelationship", propOrder = {
    "target"
})
public class ReferenceRelationship
    extends Relationship {

    @XmlElement(name = "Target", required = true)
    protected ObjectIdentity target;

    public ObjectIdentity getTarget() {
        return target;
    }

    public void setTarget(ObjectIdentity value) {
        this.target = value;
    }

}

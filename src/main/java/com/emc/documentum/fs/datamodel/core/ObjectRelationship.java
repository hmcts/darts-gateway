package com.emc.documentum.fs.datamodel.core;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectRelationship", propOrder = {
    "target"
})
public class ObjectRelationship
    extends Relationship {

    @XmlElement(name = "Target", required = true)
    protected DataObject target;

    public DataObject getTarget() {
        return target;
    }

    public void setTarget(DataObject value) {
        this.target = value;
    }

}

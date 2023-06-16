package com.emc.documentum.fs.datamodel.core;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectId")
public class ObjectId {

    @XmlAttribute(name = "id", required = true)
    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

}

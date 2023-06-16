package com.emc.documentum.fs.datamodel.core;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Permission")
public class Permission {

    @XmlAttribute(name = "type", required = true)
    protected PermissionType type;
    @XmlAttribute(name = "name", required = true)
    protected String name;

    public PermissionType getType() {
        return type;
    }

    public void setType(PermissionType value) {
        this.type = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

}

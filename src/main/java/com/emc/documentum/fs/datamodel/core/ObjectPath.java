package com.emc.documentum.fs.datamodel.core;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectPath")
public class ObjectPath {

    @XmlAttribute(name = "path")
    protected String path;

    public String getPath() {
        return path;
    }

    public void setPath(String value) {
        this.path = value;
    }

}

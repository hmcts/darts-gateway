package com.emc.documentum.fs.datamodel.core.content;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


@XmlType(name = "RenditionType")
@XmlEnum
public enum RenditionType {

    CLIENT,
    SERVER,
    PRIMARY;

    public static RenditionType fromValue(String v) {
        return valueOf(v);
    }

    public String value() {
        return name();
    }

}

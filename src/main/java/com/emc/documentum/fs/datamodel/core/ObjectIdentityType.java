package com.emc.documentum.fs.datamodel.core;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


@XmlType(name = "ObjectIdentityType")
@XmlEnum
public enum ObjectIdentityType {

    UNDEFINED,
    OBJECT_ID,
    OBJECT_KEY,
    OBJECT_PATH,
    QUALIFICATION,
    COMPOSITE_OBJECT_ID,
    STRING_URI;

    public static ObjectIdentityType fromValue(String v) {
        return valueOf(v);
    }

    public String value() {
        return name();
    }

}

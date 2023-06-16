package com.emc.documentum.fs.datamodel.core;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


@XmlType(name = "PermissionType")
@XmlEnum
public enum PermissionType {

    BASIC,
    EXTENDED,
    CUSTOM;

    public static PermissionType fromValue(String v) {
        return valueOf(v);
    }

    public String value() {
        return name();
    }

}

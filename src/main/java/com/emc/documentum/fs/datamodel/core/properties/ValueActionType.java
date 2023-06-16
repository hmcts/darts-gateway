package com.emc.documentum.fs.datamodel.core.properties;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


@XmlType(name = "ValueActionType")
@XmlEnum
public enum ValueActionType {

    APPEND,
    INSERT,
    DELETE,
    SET;

    public static ValueActionType fromValue(String v) {
        return valueOf(v);
    }

    public String value() {
        return name();
    }

}

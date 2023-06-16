package com.emc.documentum.fs.datamodel.core;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


@XmlType(name = "QualificationValueType")
@XmlEnum
public enum QualificationValueType {

    UNDEFINED,
    STRING;

    public static QualificationValueType fromValue(String v) {
        return valueOf(v);
    }

    public String value() {
        return name();
    }

}

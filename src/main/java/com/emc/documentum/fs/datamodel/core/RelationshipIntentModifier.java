package com.emc.documentum.fs.datamodel.core;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


@XmlType(name = "RelationshipIntentModifier")
@XmlEnum
public enum RelationshipIntentModifier {

    ADD,
    REMOVE;

    public static RelationshipIntentModifier fromValue(String v) {
        return valueOf(v);
    }

    public String value() {
        return name();
    }

}

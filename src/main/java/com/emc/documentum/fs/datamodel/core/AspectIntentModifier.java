package com.emc.documentum.fs.datamodel.core;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


@XmlType(name = "AspectIntentModifier")
@XmlEnum
public enum AspectIntentModifier {

    ATTACH,
    DETACH;

    public static AspectIntentModifier fromValue(String v) {
        return valueOf(v);
    }

    public String value() {
        return name();
    }

}

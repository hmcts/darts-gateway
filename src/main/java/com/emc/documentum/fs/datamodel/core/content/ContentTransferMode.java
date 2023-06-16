package com.emc.documentum.fs.datamodel.core.content;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


@XmlType(name = "ContentTransferMode")
@XmlEnum
public enum ContentTransferMode {

    @XmlEnumValue("BASE64")
    BASE_64("BASE64"),
    MTOM("MTOM"),
    UCF("UCF");
    private final String value;

    ContentTransferMode(String v) {
        value = v;
    }

    public static ContentTransferMode fromValue(String v) {
        for (ContentTransferMode c : ContentTransferMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public String value() {
        return value;
    }

}

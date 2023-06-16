package com.emc.documentum.fs.datamodel.core.properties;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NumberArrayProperty", propOrder = {
    "shortOrIntegerOrLong"
})
public class NumberArrayProperty
    extends ArrayProperty {

    @XmlElements({
        @XmlElement(name = "Short", type = Short.class, nillable = true),
        @XmlElement(name = "Integer", type = Integer.class, nillable = true),
        @XmlElement(name = "Long", type = Long.class, nillable = true),
        @XmlElement(name = "Double", type = Double.class, nillable = true)
    })
    protected List<Comparable> shortOrIntegerOrLong;

    public List<Comparable> getShortOrIntegerOrLong() {
        if (shortOrIntegerOrLong == null) {
            shortOrIntegerOrLong = new ArrayList<>();
        }
        return this.shortOrIntegerOrLong;
    }

}

package com.emc.documentum.fs.datamodel.core.properties;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StringArrayProperty", propOrder = {
    "values"
})
public class StringArrayProperty
    extends ArrayProperty {

    @XmlElement(name = "Values", nillable = true)
    protected List<String> values;

    public List<String> getValues() {
        if (values == null) {
            values = new ArrayList<>();
        }
        return this.values;
    }

}

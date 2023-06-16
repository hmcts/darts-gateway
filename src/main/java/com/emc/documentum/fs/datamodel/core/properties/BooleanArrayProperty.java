package com.emc.documentum.fs.datamodel.core.properties;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BooleanArrayProperty", propOrder = {
    "values"
})
public class BooleanArrayProperty
    extends ArrayProperty {

    @XmlElement(name = "Values", required = true, nillable = true)
    protected List<Boolean> values;

    public List<Boolean> getValues() {
        if (values == null) {
            values = new ArrayList<>();
        }
        return this.values;
    }

}

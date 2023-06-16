package com.emc.documentum.fs.datamodel.core.properties;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayProperty", propOrder = {
    "valueActions"
})
@XmlSeeAlso({
    StringArrayProperty.class,
    NumberArrayProperty.class,
    BooleanArrayProperty.class,
    DateArrayProperty.class,
    ObjectIdArrayProperty.class
})
public abstract class ArrayProperty
    extends Property {

    @XmlElement(name = "ValueActions")
    protected List<ValueAction> valueActions;

    public List<ValueAction> getValueActions() {
        if (valueActions == null) {
            valueActions = new ArrayList<>();
        }
        return this.valueActions;
    }

}

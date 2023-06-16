package com.emc.documentum.fs.datamodel.core.properties;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertySet", propOrder = {
    "properties"
})
public class PropertySet {

    @XmlElement(name = "Properties")
    protected List<Property> properties;
    @XmlAttribute(name = "isInternal", required = true)
    protected boolean isInternal;

    public List<Property> getProperties() {
        if (properties == null) {
            properties = new ArrayList<>();
        }
        return this.properties;
    }

    public boolean isIsInternal() {
        return isInternal;
    }

    public void setIsInternal(boolean value) {
        this.isInternal = value;
    }

}

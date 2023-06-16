package com.emc.documentum.fs.datamodel.core;

import com.emc.documentum.fs.datamodel.core.properties.PropertySet;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectKey", propOrder = {
    "propertySet"
})
public class ObjectKey {

    @XmlElement(name = "PropertySet")
    protected PropertySet propertySet;
    @XmlAttribute(name = "objectType", required = true)
    protected String objectType;

    public PropertySet getPropertySet() {
        return propertySet;
    }

    public void setPropertySet(PropertySet value) {
        this.propertySet = value;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String value) {
        this.objectType = value;
    }

}

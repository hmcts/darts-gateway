package com.emc.documentum.fs.datamodel.core.properties;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Property")
@XmlSeeAlso({
    StringProperty.class,
    NumberProperty.class,
    DateProperty.class,
    BooleanProperty.class,
    ObjectIdProperty.class,
    ArrayProperty.class,
    RichTextProperty.class
})
public abstract class Property {

    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "isTransient", required = true)
    protected boolean isTransient;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public boolean isIsTransient() {
        return isTransient;
    }

    public void setIsTransient(boolean value) {
        this.isTransient = value;
    }

}

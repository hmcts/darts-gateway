package com.emc.documentum.fs.datamodel.core.properties;

import com.emc.documentum.fs.datamodel.core.ObjectId;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectIdProperty", propOrder = {
    "value"
})
public class ObjectIdProperty
    extends Property {

    @XmlElement(name = "Value", required = true)
    protected ObjectId value;

    public ObjectId getValue() {
        return value;
    }

    public void setValue(ObjectId value) {
        this.value = value;
    }

}

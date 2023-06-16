package com.emc.documentum.fs.datamodel.core.properties;

import com.emc.documentum.fs.datamodel.core.RichText;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RichTextProperty", propOrder = {
    "value"
})
public class RichTextProperty
    extends Property {

    @XmlElement(name = "Value")
    protected RichText value;

    public RichText getValue() {
        return value;
    }

    public void setValue(RichText value) {
        this.value = value;
    }

}

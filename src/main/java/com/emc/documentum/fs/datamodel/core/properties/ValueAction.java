package com.emc.documentum.fs.datamodel.core.properties;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValueAction")
public class ValueAction {

    @XmlAttribute(name = "type", required = true)
    protected ValueActionType type;
    @XmlAttribute(name = "index", required = true)
    protected int index;

    public ValueActionType getType() {
        return type;
    }

    public void setType(ValueActionType value) {
        this.type = value;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int value) {
        this.index = value;
    }

}

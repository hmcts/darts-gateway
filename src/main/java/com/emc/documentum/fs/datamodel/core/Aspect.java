package com.emc.documentum.fs.datamodel.core;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Aspect")
public class Aspect {

    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "intentModifier")
    protected AspectIntentModifier intentModifier;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public AspectIntentModifier getIntentModifier() {
        return intentModifier;
    }

    public void setIntentModifier(AspectIntentModifier value) {
        this.intentModifier = value;
    }

}

package com.emc.documentum.fs.datamodel.core;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Qualification", propOrder = {
    "string"
})
public class Qualification {

    @XmlElement(name = "String")
    protected String string;
    @XmlAttribute(name = "qualificationValueType")
    protected QualificationValueType qualificationValueType;
    @XmlAttribute(name = "objectType")
    protected String objectType;

    public String getString() {
        return string;
    }

    public void setString(String value) {
        this.string = value;
    }

    public QualificationValueType getQualificationValueType() {
        return qualificationValueType;
    }

    public void setQualificationValueType(QualificationValueType value) {
        this.qualificationValueType = value;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String value) {
        this.objectType = value;
    }

}

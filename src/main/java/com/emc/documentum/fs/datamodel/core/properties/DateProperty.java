package com.emc.documentum.fs.datamodel.core.properties;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

import javax.xml.datatype.XMLGregorianCalendar;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DateProperty", propOrder = {
    "value"
})
public class DateProperty
    extends Property {

    @XmlElement(name = "Value", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar value;

    public XMLGregorianCalendar getValue() {
        return value;
    }

    public void setValue(XMLGregorianCalendar value) {
        this.value = value;
    }

}

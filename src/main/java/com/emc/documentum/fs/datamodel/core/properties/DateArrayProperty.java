package com.emc.documentum.fs.datamodel.core.properties;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DateArrayProperty", propOrder = {
    "values"
})
public class DateArrayProperty
    extends ArrayProperty {

    @XmlElement(name = "Values", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected List<XMLGregorianCalendar> values;

    public List<XMLGregorianCalendar> getValues() {
        if (values == null) {
            values = new ArrayList<>();
        }
        return this.values;
    }

}

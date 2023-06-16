package com.emc.documentum.fs.datamodel.core.content;

import jakarta.activation.DataHandler;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlMimeType;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataHandlerContent", propOrder = {
    "value"
})
public class DataHandlerContent
    extends Content {

    @XmlElement(name = "Value", required = true)
    @XmlMimeType("*/*")
    protected DataHandler value;

    public DataHandler getValue() {
        return value;
    }

    public void setValue(DataHandler value) {
        this.value = value;
    }

}

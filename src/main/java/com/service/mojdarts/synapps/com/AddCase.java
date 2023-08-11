package com.service.mojdarts.synapps.com;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addCase", propOrder = {
    "document"
})
@XmlRootElement(name = "addCase")
public class AddCase {

    protected String document;

    public String getDocument() {
        return document;
    }

    public void setDocument(String value) {
        this.document = value;
    }

}

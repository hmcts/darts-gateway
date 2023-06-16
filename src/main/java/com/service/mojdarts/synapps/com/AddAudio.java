package com.service.mojdarts.synapps.com;

import com.emc.documentum.fs.datamodel.core.DataPackage;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addAudio", propOrder = {
    "document",
    "dataPackage"
})
@XmlRootElement(name = "addAudio")
public class AddAudio {

    protected String document;
    protected DataPackage dataPackage;

    public String getDocument() {
        return document;
    }

    public void setDocument(String value) {
        this.document = value;
    }

    public DataPackage getDataPackage() {
        return dataPackage;
    }

    public void setDataPackage(DataPackage value) {
        this.dataPackage = value;
    }

}

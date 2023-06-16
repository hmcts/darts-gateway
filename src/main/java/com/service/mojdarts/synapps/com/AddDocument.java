package com.service.mojdarts.synapps.com;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addDocument", propOrder = {
    "messageId",
    "type",
    "subType",
    "document"
})
@XmlRootElement(name = "addDocument")
public class AddDocument {

    protected String messageId;
    protected String type;
    protected String subType;
    protected String document;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String value) {
        this.messageId = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String value) {
        this.type = value;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String value) {
        this.subType = value;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String value) {
        this.document = value;
    }

}

package com.emc.documentum.fs.rt;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DfsExceptionHolder", propOrder = {
    "attribute",
    "exceptionClass",
    "genericType",
    "message",
    "messageId",
    "stackTrace"
})
public class DfsExceptionHolder {

    @XmlElement(nillable = true)
    protected List<DfsAttributeHolder> attribute;
    protected String exceptionClass;
    protected String genericType;
    protected String message;
    protected String messageId;
    @XmlElement(nillable = true)
    protected List<StackTraceHolder> stackTrace;

    public List<DfsAttributeHolder> getAttribute() {
        if (attribute == null) {
            attribute = new ArrayList<>();
        }
        return this.attribute;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String value) {
        this.exceptionClass = value;
    }

    public String getGenericType() {
        return genericType;
    }

    public void setGenericType(String value) {
        this.genericType = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String value) {
        this.messageId = value;
    }

    public List<StackTraceHolder> getStackTrace() {
        if (stackTrace == null) {
            stackTrace = new ArrayList<>();
        }
        return this.stackTrace;
    }

}

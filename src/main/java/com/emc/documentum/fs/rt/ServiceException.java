package com.emc.documentum.fs.rt;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceException", propOrder = {
    "exceptionBean",
    "message",
    "messageArgs",
    "messageId",
    "stackTraceAsString"
})
public class ServiceException {

    @XmlElement(nillable = true)
    protected List<DfsExceptionHolder> exceptionBean;
    protected String message;
    @XmlElement(nillable = true)
    protected List<Object> messageArgs;
    protected String messageId;
    protected String stackTraceAsString;

    public List<DfsExceptionHolder> getExceptionBean() {
        if (exceptionBean == null) {
            exceptionBean = new ArrayList<>();
        }
        return this.exceptionBean;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public List<Object> getMessageArgs() {
        if (messageArgs == null) {
            messageArgs = new ArrayList<>();
        }
        return this.messageArgs;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String value) {
        this.messageId = value;
    }

    public String getStackTraceAsString() {
        return stackTraceAsString;
    }

    public void setStackTraceAsString(String value) {
        this.stackTraceAsString = value;
    }

}

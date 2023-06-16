package com.emc.documentum.fs.datamodel.core;

import com.emc.documentum.fs.datamodel.core.content.Content;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RichText", propOrder = {
    "body",
    "contents"
})
public class RichText {

    @XmlElement(name = "Body")
    protected Content body;
    @XmlElement(name = "Contents")
    protected List<Content> contents;
    @XmlAttribute(name = "format")
    protected String format;

    public Content getBody() {
        return body;
    }

    public void setBody(Content value) {
        this.body = value;
    }

    public List<Content> getContents() {
        if (contents == null) {
            contents = new ArrayList<>();
        }
        return this.contents;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String value) {
        this.format = value;
    }

}

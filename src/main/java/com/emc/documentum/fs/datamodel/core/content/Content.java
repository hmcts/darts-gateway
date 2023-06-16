package com.emc.documentum.fs.datamodel.core.content;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Content", propOrder = {
    "renditionType"
})
@XmlSeeAlso({
    BinaryContent.class,
    DataHandlerContent.class,
    UcfContent.class,
    UrlContent.class
})
public abstract class Content {

    @XmlElementRef(name = "renditionType", namespace = "http://content.core.datamodel.fs.documentum.emc.com/", type = JAXBElement.class, required = false)
    protected JAXBElement<RenditionType> renditionType;
    @XmlAttribute(name = "format", required = true)
    protected String format;
    @XmlAttribute(name = "pageNumber", required = true)
    protected int pageNumber;
    @XmlAttribute(name = "pageModifier")
    protected String pageModifier;
    @XmlAttribute(name = "contentTransferMode")
    protected ContentTransferMode contentTransferMode;

    public JAXBElement<RenditionType> getRenditionType() {
        return renditionType;
    }

    public void setRenditionType(JAXBElement<RenditionType> value) {
        this.renditionType = value;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String value) {
        this.format = value;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int value) {
        this.pageNumber = value;
    }

    public String getPageModifier() {
        return pageModifier;
    }

    public void setPageModifier(String value) {
        this.pageModifier = value;
    }

    public ContentTransferMode getContentTransferMode() {
        return contentTransferMode;
    }

    public void setContentTransferMode(ContentTransferMode value) {
        this.contentTransferMode = value;
    }

}

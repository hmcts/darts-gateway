package com.emc.documentum.fs.datamodel.core.content;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;

import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private static final QName _ContentRenditionType_QNAME = new QName(
        "http://content.core.datamodel.fs.documentum.emc.com/",
        "renditionType"
    );

    public ObjectFactory() {
    }

    public BinaryContent createBinaryContent() {
        return new BinaryContent();
    }

    public DataHandlerContent createDataHandlerContent() {
        return new DataHandlerContent();
    }

    public UcfContent createUcfContent() {
        return new UcfContent();
    }

    public ActivityInfo createActivityInfo() {
        return new ActivityInfo();
    }

    public UrlContent createUrlContent() {
        return new UrlContent();
    }

    @XmlElementDecl(namespace = "http://content.core.datamodel.fs.documentum.emc.com/", name = "renditionType", scope = Content.class)
    public JAXBElement<RenditionType> createContentRenditionType(RenditionType value) {
        return new JAXBElement<>(_ContentRenditionType_QNAME, RenditionType.class, Content.class, value);
    }

}

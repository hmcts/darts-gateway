package com.emc.documentum.fs.datamodel.core;

import com.emc.documentum.fs.datamodel.core.content.Content;
import com.emc.documentum.fs.datamodel.core.properties.PropertySet;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataObject", propOrder = {
    "aspects",
    "identity",
    "properties",
    "relationships",
    "contents",
    "permissions"
})
public class DataObject {

    @XmlElement(name = "Aspects")
    protected List<Aspect> aspects;
    @XmlElement(name = "Identity")
    protected ObjectIdentity identity;
    @XmlElement(name = "Properties")
    protected PropertySet properties;
    @XmlElement(name = "Relationships")
    protected List<Relationship> relationships;
    @XmlElement(name = "Contents")
    protected List<Content> contents;
    @XmlElement(name = "Permissions")
    protected List<Permission> permissions;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "transientId")
    protected String transientId;

    public List<Aspect> getAspects() {
        if (aspects == null) {
            aspects = new ArrayList<>();
        }
        return this.aspects;
    }

    public ObjectIdentity getIdentity() {
        return identity;
    }

    public void setIdentity(ObjectIdentity value) {
        this.identity = value;
    }

    public PropertySet getProperties() {
        return properties;
    }

    public void setProperties(PropertySet value) {
        this.properties = value;
    }

    public List<Relationship> getRelationships() {
        if (relationships == null) {
            relationships = new ArrayList<>();
        }
        return this.relationships;
    }

    public List<Content> getContents() {
        if (contents == null) {
            contents = new ArrayList<>();
        }
        return this.contents;
    }

    public List<Permission> getPermissions() {
        if (permissions == null) {
            permissions = new ArrayList<>();
        }
        return this.permissions;
    }

    public String getType() {
        return type;
    }

    public void setType(String value) {
        this.type = value;
    }

    public String getTransientId() {
        return transientId;
    }

    public void setTransientId(String value) {
        this.transientId = value;
    }

}

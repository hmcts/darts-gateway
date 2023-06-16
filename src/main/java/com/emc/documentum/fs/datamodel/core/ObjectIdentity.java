package com.emc.documentum.fs.datamodel.core;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectIdentity", propOrder = {
    "objectId",
    "objectKey",
    "objectPath",
    "qualification",
    "compositeObjectId",
    "stringUri"
})
public class ObjectIdentity {

    @XmlElement(name = "ObjectId")
    protected ObjectId objectId;
    @XmlElement(name = "ObjectKey")
    protected ObjectKey objectKey;
    @XmlElement(name = "ObjectPath")
    protected ObjectPath objectPath;
    @XmlElement(name = "Qualification")
    protected Qualification qualification;
    @XmlElement(name = "CompositeObjectId")
    protected CompositeObjectId compositeObjectId;
    @XmlElement(name = "StringUri")
    protected String stringUri;
    @XmlAttribute(name = "valueType")
    protected ObjectIdentityType valueType;
    @XmlAttribute(name = "repositoryName")
    protected String repositoryName;

    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId value) {
        this.objectId = value;
    }

    public ObjectKey getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(ObjectKey value) {
        this.objectKey = value;
    }

    public ObjectPath getObjectPath() {
        return objectPath;
    }

    public void setObjectPath(ObjectPath value) {
        this.objectPath = value;
    }

    public Qualification getQualification() {
        return qualification;
    }

    public void setQualification(Qualification value) {
        this.qualification = value;
    }

    public CompositeObjectId getCompositeObjectId() {
        return compositeObjectId;
    }

    public void setCompositeObjectId(CompositeObjectId value) {
        this.compositeObjectId = value;
    }

    public String getStringUri() {
        return stringUri;
    }

    public void setStringUri(String value) {
        this.stringUri = value;
    }

    public ObjectIdentityType getValueType() {
        return valueType;
    }

    public void setValueType(ObjectIdentityType value) {
        this.valueType = value;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String value) {
        this.repositoryName = value;
    }

}

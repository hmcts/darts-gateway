package com.emc.documentum.fs.datamodel.core;

import jakarta.xml.bind.annotation.XmlRegistry;


@XmlRegistry
public class ObjectFactory {


    public ObjectFactory() {
    }

    public DataPackage createDataPackage() {
        return new DataPackage();
    }

    public DataObject createDataObject() {
        return new DataObject();
    }

    public Aspect createAspect() {
        return new Aspect();
    }

    public ObjectIdentity createObjectIdentity() {
        return new ObjectIdentity();
    }

    public ObjectId createObjectId() {
        return new ObjectId();
    }

    public ObjectKey createObjectKey() {
        return new ObjectKey();
    }

    public RichText createRichText() {
        return new RichText();
    }

    public ObjectPath createObjectPath() {
        return new ObjectPath();
    }

    public Qualification createQualification() {
        return new Qualification();
    }

    public CompositeObjectId createCompositeObjectId() {
        return new CompositeObjectId();
    }

    public ReferenceRelationship createReferenceRelationship() {
        return new ReferenceRelationship();
    }

    public ObjectRelationship createObjectRelationship() {
        return new ObjectRelationship();
    }

    public Permission createPermission() {
        return new Permission();
    }

}

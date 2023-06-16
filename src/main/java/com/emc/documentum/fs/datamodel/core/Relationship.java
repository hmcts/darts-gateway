package com.emc.documentum.fs.datamodel.core;

import com.emc.documentum.fs.datamodel.core.properties.PropertySet;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Relationship", propOrder = {
    "relationshipProperties"
})
@XmlSeeAlso({
    ReferenceRelationship.class,
    ObjectRelationship.class
})
public abstract class Relationship {

    @XmlElement(name = "RelationshipProperties")
    protected PropertySet relationshipProperties;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "targetRole", required = true)
    protected String targetRole;
    @XmlAttribute(name = "intentModifier")
    protected RelationshipIntentModifier intentModifier;

    public PropertySet getRelationshipProperties() {
        return relationshipProperties;
    }

    public void setRelationshipProperties(PropertySet value) {
        this.relationshipProperties = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String value) {
        this.targetRole = value;
    }

    public RelationshipIntentModifier getIntentModifier() {
        return intentModifier;
    }

    public void setIntentModifier(RelationshipIntentModifier value) {
        this.intentModifier = value;
    }

}

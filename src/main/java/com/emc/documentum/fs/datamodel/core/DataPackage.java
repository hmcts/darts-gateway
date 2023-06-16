package com.emc.documentum.fs.datamodel.core;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataPackage", propOrder = {
    "dataObjects"
})
public class DataPackage {

    @XmlElement(name = "DataObjects")
    protected List<DataObject> dataObjects;
    @XmlAttribute(name = "repositoryName")
    protected String repositoryName;

    public List<DataObject> getDataObjects() {
        if (dataObjects == null) {
            dataObjects = new ArrayList<>();
        }
        return this.dataObjects;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String value) {
        this.repositoryName = value;
    }

}

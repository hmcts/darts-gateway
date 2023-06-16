package com.emc.documentum.fs.datamodel.core.properties;

import com.emc.documentum.fs.datamodel.core.ObjectId;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectIdArrayProperty", propOrder = {
    "values"
})
public class ObjectIdArrayProperty
    extends ArrayProperty {

    @XmlElement(name = "Values", required = true, nillable = true)
    protected List<ObjectId> values;

    public List<ObjectId> getValues() {
        if (values == null) {
            values = new ArrayList<>();
        }
        return this.values;
    }

}

package com.emc.documentum.fs.datamodel.core.properties;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NumberProperty", propOrder = {
    "_short",
    "integer",
    "_long",
    "_double"
})
public class NumberProperty
    extends Property {

    @XmlElement(name = "Short")
    protected Short _short;
    @XmlElement(name = "Integer")
    protected Integer integer;
    @XmlElement(name = "Long")
    protected Long _long;
    @XmlElement(name = "Double")
    protected Double _double;

    public Short getShort() {
        return _short;
    }

    public void setShort(Short value) {
        this._short = value;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer value) {
        this.integer = value;
    }

    public Long getLong() {
        return _long;
    }

    public void setLong(Long value) {
        this._long = value;
    }

    public Double getDouble() {
        return _double;
    }

    public void setDouble(Double value) {
        this._double = value;
    }

}

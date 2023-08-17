package com.synapps.moj.dfs.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CourtLogEntry", propOrder = {
    "value"
})
public class CourtLogEntry {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "Y")
    protected String y;
    @XmlAttribute(name = "M")
    protected String m;
    @XmlAttribute(name = "D")
    protected String d;
    @XmlAttribute(name = "H")
    protected String h;
    @XmlAttribute(name = "MIN")
    protected String min;
    @XmlAttribute(name = "S")
    protected String s;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getY() {
        return y;
    }

    public void setY(String value) {
        this.y = value;
    }

    public String getM() {
        return m;
    }

    public void setM(String value) {
        this.m = value;
    }

    public String getD() {
        return d;
    }

    public void setD(String value) {
        this.d = value;
    }

    public String getH() {
        return h;
    }

    public void setH(String value) {
        this.h = value;
    }

    public String getMIN() {
        return min;
    }

    public void setMIN(String value) {
        this.min = value;
    }

    public String getS() {
        return s;
    }

    public void setS(String value) {
        this.s = value;
    }

}

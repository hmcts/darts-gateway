package com.synapps.moj.dfs.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cases", propOrder = {
    "_case"
})
public class Cases {

    @XmlElement(name = "case")
    protected List<Case> _case;
    @XmlAttribute(name = "courthouse")
    protected String courthouse;
    @XmlAttribute(name = "courtroom")
    protected String courtroom;
    @XmlAttribute(name = "Y")
    protected String y;
    @XmlAttribute(name = "M")
    protected String m;
    @XmlAttribute(name = "D")
    protected String d;

    public List<Case> getCase() {
        if (_case == null) {
            _case = new ArrayList<>();
        }
        return this._case;
    }

    public String getCourthouse() {
        return courthouse;
    }

    public void setCourthouse(String value) {
        this.courthouse = value;
    }

    public String getCourtroom() {
        return courtroom;
    }

    public void setCourtroom(String value) {
        this.courtroom = value;
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

}

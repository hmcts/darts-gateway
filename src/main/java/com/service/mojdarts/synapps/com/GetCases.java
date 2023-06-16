package com.service.mojdarts.synapps.com;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getCases", propOrder = {
    "courthouse",
    "courtroom",
    "date"
})
public class GetCases {

    protected String courthouse;
    protected String courtroom;
    protected String date;

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

    public String getDate() {
        return date;
    }

    public void setDate(String value) {
        this.date = value;
    }

}

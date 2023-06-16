package com.service.mojdarts.synapps.com;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getCourtLog", propOrder = {
    "courthouse",
    "caseNumber",
    "startTime",
    "endTime"
})
public class GetCourtLog {

    protected String courthouse;
    protected String caseNumber;
    protected String startTime;
    protected String endTime;

    public String getCourthouse() {
        return courthouse;
    }

    public void setCourthouse(String value) {
        this.courthouse = value;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String value) {
        this.caseNumber = value;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String value) {
        this.startTime = value;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String value) {
        this.endTime = value;
    }

}

package com.synapps.moj.dfs.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "court_log", propOrder = {
    "entry"
})
public class CourtLog {

    protected List<CourtLogEntry> entry;
    @XmlAttribute(name = "courthouse")
    protected String courthouse;
    @XmlAttribute(name = "case_number")
    protected String caseNumber;

    public List<CourtLogEntry> getEntry() {
        if (entry == null) {
            entry = new ArrayList<>();
        }
        return this.entry;
    }

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

    public void addCourtLogEntry(CourtLogEntry courtLogEntry) {
        if(isNull(entry)) {
            entry = new ArrayList<>();
        }
        entry.add(courtLogEntry);
    }
}

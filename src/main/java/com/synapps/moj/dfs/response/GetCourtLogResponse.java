package com.synapps.moj.dfs.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCourtLogResponse", propOrder = {
    "courtLog",
    "entries"
})
public class GetCourtLogResponse
    extends DARTSResponse {

    @XmlElement(name = "court_log")
    protected CourtLog courtLog;
    @XmlElement(nillable = true)
    protected List<CourtLogEntry> entries;

    public CourtLog getCourtLog() {
        return courtLog;
    }

    public void setCourtLog(CourtLog value) {
        this.courtLog = value;
    }

    public List<CourtLogEntry> getEntries() {
        if (entries == null) {
            entries = new ArrayList<>();
        }
        return this.entries;
    }

}

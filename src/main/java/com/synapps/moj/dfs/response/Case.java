package com.synapps.moj.dfs.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Case", propOrder = {
    "caseNumber",
    "scheduledStart",
    "uploadPriority",
    "defendants",
    "judges",
    "prosecutors",
    "defenders"
})
public class Case {

    @XmlElement(name = "case_number")
    protected String caseNumber;
    @XmlElement(name = "scheduled_start")
    protected String scheduledStart;
    @XmlElement(name = "upload_priority")
    protected String uploadPriority;
    protected Defendants defendants;
    protected Judges judges;
    protected Prosecutors prosecutors;
    protected Defenders defenders;

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String value) {
        this.caseNumber = value;
    }

    public String getScheduledStart() {
        return scheduledStart;
    }

    public void setScheduledStart(String value) {
        this.scheduledStart = value;
    }

    public String getUploadPriority() {
        return uploadPriority;
    }

    public void setUploadPriority(String value) {
        this.uploadPriority = value;
    }

    public Defendants getDefendants() {
        return defendants;
    }

    public void setDefendants(Defendants value) {
        this.defendants = value;
    }

    public Judges getJudges() {
        return judges;
    }

    public void setJudges(Judges value) {
        this.judges = value;
    }

    public Prosecutors getProsecutors() {
        return prosecutors;
    }

    public void setProsecutors(Prosecutors value) {
        this.prosecutors = value;
    }

    public Defenders getDefenders() {
        return defenders;
    }

    public void setDefenders(Defenders value) {
        this.defenders = value;
    }

}

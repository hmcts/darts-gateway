package uk.gov.courtservice.events;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "courtHouse",
    "courtRoom",
    "caseNumbers",
    "eventText",
    "retentionPolicy"
})
@XmlRootElement(name = "DartsEvent")
public class DartsEvent {

    @XmlElement(name = "CourtHouse", required = true)
    protected String courtHouse;
    @XmlElement(name = "CourtRoom")
    protected String courtRoom;
    @XmlElement(name = "CaseNumbers", required = true)
    protected DartsEvent.CaseNumbers caseNumbers;
    @XmlElement(name = "EventText")
    protected String eventText;
    @XmlAttribute(name = "ID")
    protected BigInteger id;
    @XmlAttribute(name = "Y", required = true)
    protected BigInteger y;
    @XmlAttribute(name = "M", required = true)
    protected BigInteger m;
    @XmlAttribute(name = "D", required = true)
    protected BigInteger d;
    @XmlAttribute(name = "H", required = true)
    protected BigInteger h;
    @XmlAttribute(name = "MIN", required = true)
    protected BigInteger min;
    @XmlAttribute(name = "S", required = true)
    protected BigInteger s;

    @XmlElement(name = "RetentionPolicy")
    protected DartsEvent.RetentionPolicy retentionPolicy;

    public String getCourtHouse() {
        return courtHouse;
    }

    public void setCourtHouse(String value) {
        this.courtHouse = value;
    }

    public String getCourtRoom() {
        return courtRoom;
    }

    public void setCourtRoom(String value) {
        this.courtRoom = value;
    }

    public DartsEvent.CaseNumbers getCaseNumbers() {
        return caseNumbers;
    }

    public void setCaseNumbers(DartsEvent.CaseNumbers value) {
        this.caseNumbers = value;
    }

    public String getEventText() {
        return eventText;
    }

    public void setEventText(String value) {
        this.eventText = value;
    }

    public BigInteger getID() {
        return id;
    }

    public void setID(BigInteger value) {
        this.id = value;
    }

    public BigInteger getY() {
        return y;
    }

    public void setY(BigInteger value) {
        this.y = value;
    }

    public BigInteger getM() {
        return m;
    }

    public void setM(BigInteger value) {
        this.m = value;
    }

    public BigInteger getD() {
        return d;
    }

    public void setD(BigInteger value) {
        this.d = value;
    }

    public BigInteger getH() {
        return h;
    }

    public void setH(BigInteger value) {
        this.h = value;
    }

    public BigInteger getMIN() {
        return min;
    }

    public void setMIN(BigInteger value) {
        this.min = value;
    }

    public BigInteger getS() {
        return s;
    }

    public void setS(BigInteger value) {
        this.s = value;
    }

    public DartsEvent.RetentionPolicy getRetentionPolicy() {
        return retentionPolicy;
    }

    public void setRetentionPolicy(DartsEvent.RetentionPolicy retentionPolicy) {
        this.retentionPolicy = retentionPolicy ;
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "caseNumber"
    })
    public static class CaseNumbers {

        @XmlElement(name = "CaseNumber", required = true)
        public List<String> caseNumber;

        public List<String> getCaseNumber() {
            if (caseNumber == null) {
                caseNumber = new ArrayList<String>();
            }
            return this.caseNumber;
        }

    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "caseRetentionFixedPolicy",
        "caseTotalSentence"
    })
    public static class RetentionPolicy {

        @XmlElement(name = "CaseRetentionFixedPolicy")
        public String caseRetentionFixedPolicy;

        @XmlElement(name = "CaseTotalSentence")
        public String caseTotalSentence;

        public String getCaseRetentionFixedPolicy() {
            return caseRetentionFixedPolicy;
        }

        public void setCaseRetentionFixedPolicy(String caseRetentionFixedPolicy) {
            this.caseRetentionFixedPolicy = caseRetentionFixedPolicy ;
        }

        public String getCaseTotalSentence() {
            return caseTotalSentence;
        }

        public void setCaseTotalSentence(String caseTotalSentence) {
            this.caseTotalSentence = caseTotalSentence ;
        }
    }

}

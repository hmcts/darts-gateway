
package uk.gov.addcase;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "courthouse",
    "courtroom",
    "defendants",
    "judges",
    "prosecutors",
    "defenders"
})
@XmlRootElement(name = "case")
public class Case {

    @XmlElement(required = true)
    protected String courthouse;
    protected String courtroom;
    protected List<Defendants> defendants;
    protected List<Judges> judges;
    protected List<Prosecutors> prosecutors;
    protected List<Defenders> defenders;
    @XmlAttribute(name = "type", required = true)
    protected String type;
    @XmlAttribute(name = "id", required = true)
    protected String id;

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

    public List<Defendants> getDefendants() {
        if (defendants == null) {
            defendants = new ArrayList<Defendants>();
        }
        return this.defendants;
    }

    public List<Judges> getJudges() {
        if (judges == null) {
            judges = new ArrayList<Judges>();
        }
        return this.judges;
    }

    public List<Prosecutors> getProsecutors() {
        if (prosecutors == null) {
            prosecutors = new ArrayList<Prosecutors>();
        }
        return this.prosecutors;
    }

    public List<Defenders> getDefenders() {
        if (defenders == null) {
            defenders = new ArrayList<Defenders>();
        }
        return this.defenders;
    }

    public String getType() {
        return type;
    }

    public void setType(String value) {
        this.type = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "defendant"
    })
    public static class Defendants {

        @XmlElement(nillable = true)
        protected List<String> defendant;

        public List<String> getDefendant() {
            if (defendant == null) {
                defendant = new ArrayList<String>();
            }
            return this.defendant;
        }

    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "defender"
    })
    public static class Defenders {

        @XmlElement(nillable = true)
        protected List<String> defender;

        public List<String> getDefender() {
            if (defender == null) {
                defender = new ArrayList<String>();
            }
            return this.defender;
        }

    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "judge"
    })
    public static class Judges {

        @XmlElement(nillable = true)
        protected List<String> judge;

        public List<String> getJudge() {
            if (judge == null) {
                judge = new ArrayList<String>();
            }
            return this.judge;
        }

    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "prosecutor"
    })
    public static class Prosecutors {

        @XmlElement(nillable = true)
        protected List<String> prosecutor;

        public List<String> getProsecutor() {
            if (prosecutor == null) {
                prosecutor = new ArrayList<String>();
            }
            return this.prosecutor;
        }

    }

}

package uk.gov.courtservice.events;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "dartsEvent"
})
@XmlRootElement(name = "NewDataSet")
public class NewDataSet {

    @XmlElement(name = "DartsEvent")
    protected List<DartsEvent> dartsEvent;

    public List<DartsEvent> getDartsEvent() {
        if (dartsEvent == null) {
            dartsEvent = new ArrayList<DartsEvent>();
        }
        return this.dartsEvent;
    }

}

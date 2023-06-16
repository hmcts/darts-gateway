package uk.gov.courtservice.events;

import jakarta.xml.bind.annotation.XmlRegistry;


@XmlRegistry
public class ObjectFactory {


    public ObjectFactory() {
    }

    public DartsEvent createDartsEvent() {
        return new DartsEvent();
    }

    public DartsEvent.CaseNumbers createDartsEventCaseNumbers() {
        return new DartsEvent.CaseNumbers();
    }

    public NewDataSet createNewDataSet() {
        return new NewDataSet();
    }

}

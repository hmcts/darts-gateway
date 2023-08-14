
package uk.gov.addcase;

import jakarta.xml.bind.annotation.XmlRegistry;


@XmlRegistry
public class ObjectFactory {


    public ObjectFactory() {
    }

    public Case createCase() {
        return new Case();
    }

    public Case.Defendants createCaseDefendants() {
        return new Case.Defendants();
    }

    public Case.Judges createCaseJudges() {
        return new Case.Judges();
    }

    public Case.Prosecutors createCaseProsecutors() {
        return new Case.Prosecutors();
    }

    public Case.Defenders createCaseDefenders() {
        return new Case.Defenders();
    }

    public NewDataSet createNewDataSet() {
        return new NewDataSet();
    }

}

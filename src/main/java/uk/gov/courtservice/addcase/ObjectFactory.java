
package uk.gov.courtservice.addcase;

import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uk.gov.courtservice.addcase package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v3.0.2", date = "2023-05-15T13:36:48+01:00")
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uk.gov.courtservice.addcase
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Case }
     * 
     */
    public Case createCase() {
        return new Case();
    }

    /**
     * Create an instance of {@link Case.Defendants }
     * 
     */
    public Case.Defendants createCaseDefendants() {
        return new Case.Defendants();
    }

    /**
     * Create an instance of {@link Case.Judges }
     * 
     */
    public Case.Judges createCaseJudges() {
        return new Case.Judges();
    }

    /**
     * Create an instance of {@link Case.Prosecutors }
     * 
     */
    public Case.Prosecutors createCaseProsecutors() {
        return new Case.Prosecutors();
    }

    /**
     * Create an instance of {@link Case.Defenders }
     * 
     */
    public Case.Defenders createCaseDefenders() {
        return new Case.Defenders();
    }

    /**
     * Create an instance of {@link NewDataSet }
     * 
     */
    public NewDataSet createNewDataSet() {
        return new NewDataSet();
    }

}

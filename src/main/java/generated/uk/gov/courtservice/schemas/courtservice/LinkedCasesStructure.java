//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.3
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
//


package uk.gov.courtservice.schemas.courtservice;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for LinkedCasesStructure complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="LinkedCasesStructure">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="CaseNumber" type="{http://www.courtservice.gov.uk/schemas/courtservice}CaseLinkRefType" maxOccurs="unbounded"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LinkedCasesStructure", propOrder = {
    "caseNumber"
})
public class LinkedCasesStructure {

    @XmlElement(name = "CaseNumber", required = true)
    protected List<String> caseNumber;

    /**
     * Gets the value of the caseNumber property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the caseNumber property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCaseNumber().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     * @return
     *     The value of the caseNumber property.
     */
    public List<String> getCaseNumber() {
        if (caseNumber == null) {
            caseNumber = new ArrayList<>();
        }
        return this.caseNumber;
    }

}
//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.3
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
//


package uk.gov.govtalk.people.bs7666;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlList;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for StreetStructure complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="StreetStructure">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="StreetReferenceType" type="{http://www.govtalk.gov.uk/people/bs7666}StreetReferenceTypeType"/>
 *         <element name="StartCoordinate" type="{http://www.govtalk.gov.uk/people/bs7666}CoordinateStructure"/>
 *         <element name="EndCoordinate" type="{http://www.govtalk.gov.uk/people/bs7666}CoordinateStructure"/>
 *         <element name="Tolerance" type="{http://www.govtalk.gov.uk/people/bs7666}ToleranceType"/>
 *         <element name="StreetVersionNumber" type="{http://www.govtalk.gov.uk/people/bs7666}VersionNumberType"/>
 *         <element name="StreetEntryDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         <element name="StreetClosureDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         <element name="ResponsibleAuthority" type="{http://www.govtalk.gov.uk/people/bs7666}CustodianCodeType"/>
 *         <element name="DescriptiveIdentifier" type="{http://www.govtalk.gov.uk/people/bs7666}StreetDescriptiveIdentifierStructure"/>
 *         <element name="StreetAlias" type="{http://www.govtalk.gov.uk/people/bs7666}StreetDescriptiveIdentifierStructure" minOccurs="0"/>
 *         <element name="StreetCrossReferences" minOccurs="0">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <choice>
 *                   <element name="UNIQUE_STREET_REFERENCE_NUMBERS" type="{http://www.govtalk.gov.uk/people/bs7666}USRNlistType"/>
 *                   <element name="ElementaryStreetUnit" type="{http://www.govtalk.gov.uk/people/bs7666}ElementaryStreetUnitStructure" maxOccurs="unbounded" minOccurs="0"/>
 *                 </choice>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *       </sequence>
 *       <attribute name="usrn" type="{http://www.govtalk.gov.uk/people/bs7666}USRNtype" />
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StreetStructure", propOrder = {
    "streetReferenceType",
    "startCoordinate",
    "endCoordinate",
    "tolerance",
    "streetVersionNumber",
    "streetEntryDate",
    "streetClosureDate",
    "responsibleAuthority",
    "descriptiveIdentifier",
    "streetAlias",
    "streetCrossReferences"
})
public class StreetStructure {

    @XmlElement(name = "StreetReferenceType", required = true)
    protected BigInteger streetReferenceType;
    @XmlElement(name = "StartCoordinate", required = true)
    protected CoordinateStructure startCoordinate;
    @XmlElement(name = "EndCoordinate", required = true)
    protected CoordinateStructure endCoordinate;
    @XmlElement(name = "Tolerance", required = true)
    protected BigDecimal tolerance;
    @XmlElement(name = "StreetVersionNumber")
    @XmlSchemaType(name = "positiveInteger")
    protected int streetVersionNumber;
    @XmlElement(name = "StreetEntryDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar streetEntryDate;
    @XmlElement(name = "StreetClosureDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar streetClosureDate;
    @XmlElement(name = "ResponsibleAuthority")
    @XmlSchemaType(name = "integer")
    protected int responsibleAuthority;
    @XmlElement(name = "DescriptiveIdentifier", required = true)
    protected StreetDescriptiveIdentifierStructure descriptiveIdentifier;
    @XmlElement(name = "StreetAlias")
    protected StreetDescriptiveIdentifierStructure streetAlias;
    @XmlElement(name = "StreetCrossReferences")
    protected StreetCrossReferences streetCrossReferences;
    @XmlAttribute(name = "usrn")
    protected Integer usrn;

    /**
     * Gets the value of the streetReferenceType property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getStreetReferenceType() {
        return streetReferenceType;
    }

    /**
     * Sets the value of the streetReferenceType property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setStreetReferenceType(BigInteger value) {
        this.streetReferenceType = value;
    }

    /**
     * Gets the value of the startCoordinate property.
     *
     * @return
     *     possible object is
     *     {@link CoordinateStructure }
     *
     */
    public CoordinateStructure getStartCoordinate() {
        return startCoordinate;
    }

    /**
     * Sets the value of the startCoordinate property.
     *
     * @param value
     *     allowed object is
     *     {@link CoordinateStructure }
     *
     */
    public void setStartCoordinate(CoordinateStructure value) {
        this.startCoordinate = value;
    }

    /**
     * Gets the value of the endCoordinate property.
     *
     * @return
     *     possible object is
     *     {@link CoordinateStructure }
     *
     */
    public CoordinateStructure getEndCoordinate() {
        return endCoordinate;
    }

    /**
     * Sets the value of the endCoordinate property.
     *
     * @param value
     *     allowed object is
     *     {@link CoordinateStructure }
     *
     */
    public void setEndCoordinate(CoordinateStructure value) {
        this.endCoordinate = value;
    }

    /**
     * Gets the value of the tolerance property.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getTolerance() {
        return tolerance;
    }

    /**
     * Sets the value of the tolerance property.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setTolerance(BigDecimal value) {
        this.tolerance = value;
    }

    /**
     * Gets the value of the streetVersionNumber property.
     *
     */
    public int getStreetVersionNumber() {
        return streetVersionNumber;
    }

    /**
     * Sets the value of the streetVersionNumber property.
     *
     */
    public void setStreetVersionNumber(int value) {
        this.streetVersionNumber = value;
    }

    /**
     * Gets the value of the streetEntryDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getStreetEntryDate() {
        return streetEntryDate;
    }

    /**
     * Sets the value of the streetEntryDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setStreetEntryDate(XMLGregorianCalendar value) {
        this.streetEntryDate = value;
    }

    /**
     * Gets the value of the streetClosureDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getStreetClosureDate() {
        return streetClosureDate;
    }

    /**
     * Sets the value of the streetClosureDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setStreetClosureDate(XMLGregorianCalendar value) {
        this.streetClosureDate = value;
    }

    /**
     * Gets the value of the responsibleAuthority property.
     *
     */
    public int getResponsibleAuthority() {
        return responsibleAuthority;
    }

    /**
     * Sets the value of the responsibleAuthority property.
     *
     */
    public void setResponsibleAuthority(int value) {
        this.responsibleAuthority = value;
    }

    /**
     * Gets the value of the descriptiveIdentifier property.
     *
     * @return
     *     possible object is
     *     {@link StreetDescriptiveIdentifierStructure }
     *
     */
    public StreetDescriptiveIdentifierStructure getDescriptiveIdentifier() {
        return descriptiveIdentifier;
    }

    /**
     * Sets the value of the descriptiveIdentifier property.
     *
     * @param value
     *     allowed object is
     *     {@link StreetDescriptiveIdentifierStructure }
     *
     */
    public void setDescriptiveIdentifier(StreetDescriptiveIdentifierStructure value) {
        this.descriptiveIdentifier = value;
    }

    /**
     * Gets the value of the streetAlias property.
     *
     * @return
     *     possible object is
     *     {@link StreetDescriptiveIdentifierStructure }
     *
     */
    public StreetDescriptiveIdentifierStructure getStreetAlias() {
        return streetAlias;
    }

    /**
     * Sets the value of the streetAlias property.
     *
     * @param value
     *     allowed object is
     *     {@link StreetDescriptiveIdentifierStructure }
     *
     */
    public void setStreetAlias(StreetDescriptiveIdentifierStructure value) {
        this.streetAlias = value;
    }

    /**
     * Gets the value of the streetCrossReferences property.
     *
     * @return
     *     possible object is
     *     {@link StreetCrossReferences }
     *
     */
    public StreetCrossReferences getStreetCrossReferences() {
        return streetCrossReferences;
    }

    /**
     * Sets the value of the streetCrossReferences property.
     *
     * @param value
     *     allowed object is
     *     {@link StreetCrossReferences }
     *
     */
    public void setStreetCrossReferences(StreetCrossReferences value) {
        this.streetCrossReferences = value;
    }

    /**
     * Gets the value of the usrn property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getUsrn() {
        return usrn;
    }

    /**
     * Sets the value of the usrn property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setUsrn(Integer value) {
        this.usrn = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <choice>
     *         <element name="UNIQUE_STREET_REFERENCE_NUMBERS" type="{http://www.govtalk.gov.uk/people/bs7666}USRNlistType"/>
     *         <element name="ElementaryStreetUnit" type="{http://www.govtalk.gov.uk/people/bs7666}ElementaryStreetUnitStructure" maxOccurs="unbounded" minOccurs="0"/>
     *       </choice>
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "uniquestreetreferencenumbers",
        "elementaryStreetUnit"
    })
    public static class StreetCrossReferences {

        @XmlList
        @XmlElement(name = "UNIQUE_STREET_REFERENCE_NUMBERS", type = Integer.class)
        protected List<Integer> uniquestreetreferencenumbers;
        @XmlElement(name = "ElementaryStreetUnit")
        protected List<ElementaryStreetUnitStructure> elementaryStreetUnit;

        /**
         * Gets the value of the uniquestreetreferencenumbers property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the uniquestreetreferencenumbers property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getUNIQUESTREETREFERENCENUMBERS().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Integer }
         *
         *
         * @return
         *     The value of the uniquestreetreferencenumbers property.
         */
        public List<Integer> getUNIQUESTREETREFERENCENUMBERS() {
            if (uniquestreetreferencenumbers == null) {
                uniquestreetreferencenumbers = new ArrayList<>();
            }
            return this.uniquestreetreferencenumbers;
        }

        /**
         * Gets the value of the elementaryStreetUnit property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the elementaryStreetUnit property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getElementaryStreetUnit().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ElementaryStreetUnitStructure }
         *
         *
         * @return
         *     The value of the elementaryStreetUnit property.
         */
        public List<ElementaryStreetUnitStructure> getElementaryStreetUnit() {
            if (elementaryStreetUnit == null) {
                elementaryStreetUnit = new ArrayList<>();
            }
            return this.elementaryStreetUnit;
        }

    }

}
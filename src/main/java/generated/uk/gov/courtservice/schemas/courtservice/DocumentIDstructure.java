//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.3
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
//


package uk.gov.courtservice.schemas.courtservice;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import uk.gov.govtalk.people.addressandpersonaldetails.YesNoType;

import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for DocumentIDstructure complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="DocumentIDstructure">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="DocumentName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="UniqueID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="DocumentType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="TimeStamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         <element name="Version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="SecurityClassification" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="SellByDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="XSLstylesheetURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="WelshTranslation" type="{http://www.courtservice.gov.uk/schemas/courtservice}YesNoType" minOccurs="0"/>
 *         <element name="DocumentInformation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentIDstructure", propOrder = {
    "documentName",
    "uniqueID",
    "documentType",
    "timeStamp",
    "version",
    "securityClassification",
    "sellByDate",
    "xsLstylesheetURL",
    "welshTranslation",
    "documentInformation"
})
public class DocumentIDstructure {

    @XmlElement(name = "DocumentName", required = true)
    protected String documentName;
    @XmlElement(name = "UniqueID", required = true)
    protected String uniqueID;
    @XmlElement(name = "DocumentType", required = true)
    protected String documentType;
    @XmlElement(name = "TimeStamp")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timeStamp;
    @XmlElement(name = "Version")
    protected String version;
    @XmlElement(name = "SecurityClassification")
    protected String securityClassification;
    @XmlElement(name = "SellByDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar sellByDate;
    @XmlElement(name = "XSLstylesheetURL")
    protected String xsLstylesheetURL;
    @XmlElement(name = "WelshTranslation")
    @XmlSchemaType(name = "string")
    protected YesNoType welshTranslation;
    @XmlElement(name = "DocumentInformation")
    protected String documentInformation;

    /**
     * Gets the value of the documentName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDocumentName() {
        return documentName;
    }

    /**
     * Sets the value of the documentName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDocumentName(String value) {
        this.documentName = value;
    }

    /**
     * Gets the value of the uniqueID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUniqueID() {
        return uniqueID;
    }

    /**
     * Sets the value of the uniqueID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUniqueID(String value) {
        this.uniqueID = value;
    }

    /**
     * Gets the value of the documentType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDocumentType() {
        return documentType;
    }

    /**
     * Sets the value of the documentType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDocumentType(String value) {
        this.documentType = value;
    }

    /**
     * Gets the value of the timeStamp property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getTimeStamp() {
        return timeStamp;
    }

    /**
     * Sets the value of the timeStamp property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setTimeStamp(XMLGregorianCalendar value) {
        this.timeStamp = value;
    }

    /**
     * Gets the value of the version property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the securityClassification property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSecurityClassification() {
        return securityClassification;
    }

    /**
     * Sets the value of the securityClassification property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSecurityClassification(String value) {
        this.securityClassification = value;
    }

    /**
     * Gets the value of the sellByDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getSellByDate() {
        return sellByDate;
    }

    /**
     * Sets the value of the sellByDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setSellByDate(XMLGregorianCalendar value) {
        this.sellByDate = value;
    }

    /**
     * Gets the value of the xsLstylesheetURL property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getXSLstylesheetURL() {
        return xsLstylesheetURL;
    }

    /**
     * Sets the value of the xsLstylesheetURL property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setXSLstylesheetURL(String value) {
        this.xsLstylesheetURL = value;
    }

    /**
     * Gets the value of the welshTranslation property.
     *
     * @return
     *     possible object is
     *     {@link YesNoType }
     *
     */
    public YesNoType getWelshTranslation() {
        return welshTranslation;
    }

    /**
     * Sets the value of the welshTranslation property.
     *
     * @param value
     *     allowed object is
     *     {@link YesNoType }
     *
     */
    public void setWelshTranslation(YesNoType value) {
        this.welshTranslation = value;
    }

    /**
     * Gets the value of the documentInformation property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDocumentInformation() {
        return documentInformation;
    }

    /**
     * Sets the value of the documentInformation property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDocumentInformation(String value) {
        this.documentInformation = value;
    }

}
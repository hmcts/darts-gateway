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

import javax.xml.datatype.Duration;


/**
 * <p>Java class for IndeterminateSentenceStructure complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="IndeterminateSentenceStructure">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="MinimumSentence" type="{http://www.w3.org/2001/XMLSchema}duration"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IndeterminateSentenceStructure", propOrder = {
    "minimumSentence"
})
public class IndeterminateSentenceStructure {

    @XmlElement(name = "MinimumSentence", required = true)
    protected Duration minimumSentence;

    /**
     * Gets the value of the minimumSentence property.
     *
     * @return
     *     possible object is
     *     {@link Duration }
     *
     */
    public Duration getMinimumSentence() {
        return minimumSentence;
    }

    /**
     * Sets the value of the minimumSentence property.
     *
     * @param value
     *     allowed object is
     *     {@link Duration }
     *
     */
    public void setMinimumSentence(Duration value) {
        this.minimumSentence = value;
    }

}
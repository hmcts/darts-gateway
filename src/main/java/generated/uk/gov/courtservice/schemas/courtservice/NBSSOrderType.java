//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.3 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
//


package uk.gov.courtservice.schemas.courtservice;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NBSSOrderType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>{@code
 * <simpleType name="NBSSOrderType">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="SuspendedSentenceOriginalTerm"/>
 *     <enumeration value="SuspendedSentenceSubstitute"/>
 *     <enumeration value="Fine"/>
 *     <enumeration value="CommunityRequirement"/>
 *     <enumeration value="SupervisionalPeriod"/>
 *     <enumeration value="OperationPeriod"/>
 *   </restriction>
 * </simpleType>
 * }</pre>
 * 
 */
@XmlType(name = "NBSSOrderType")
@XmlEnum
public enum NBSSOrderType {

    @XmlEnumValue("SuspendedSentenceOriginalTerm")
    SUSPENDED_SENTENCE_ORIGINAL_TERM("SuspendedSentenceOriginalTerm"),
    @XmlEnumValue("SuspendedSentenceSubstitute")
    SUSPENDED_SENTENCE_SUBSTITUTE("SuspendedSentenceSubstitute"),
    @XmlEnumValue("Fine")
    FINE("Fine"),
    @XmlEnumValue("CommunityRequirement")
    COMMUNITY_REQUIREMENT("CommunityRequirement"),
    @XmlEnumValue("SupervisionalPeriod")
    SUPERVISIONAL_PERIOD("SupervisionalPeriod"),
    @XmlEnumValue("OperationPeriod")
    OPERATION_PERIOD("OperationPeriod");
    private final String value;

    NBSSOrderType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static NBSSOrderType fromValue(String v) {
        for (NBSSOrderType c: NBSSOrderType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
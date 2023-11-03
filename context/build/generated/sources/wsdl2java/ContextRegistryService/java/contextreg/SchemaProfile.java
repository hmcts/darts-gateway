
package contextreg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SchemaProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SchemaProfile"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://profiles.core.datamodel.fs.documentum.emc.com/}Profile"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="includeTypes" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="includeProperties" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="propertyFilter" type="{http://schema.core.datamodel.fs.documentum.emc.com/}PropertyInfoFilter" /&gt;
 *       &lt;attribute name="includeValues" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="includeRelationships" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="scope" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SchemaProfile")
public class SchemaProfile
    extends Profile
{

    @XmlAttribute(name = "includeTypes", required = true)
    protected boolean includeTypes;
    @XmlAttribute(name = "includeProperties", required = true)
    protected boolean includeProperties;
    @XmlAttribute(name = "propertyFilter")
    protected PropertyInfoFilter propertyFilter;
    @XmlAttribute(name = "includeValues", required = true)
    protected boolean includeValues;
    @XmlAttribute(name = "includeRelationships", required = true)
    protected boolean includeRelationships;
    @XmlAttribute(name = "scope")
    protected String scope;

    /**
     * Gets the value of the includeTypes property.
     * 
     */
    public boolean isIncludeTypes() {
        return includeTypes;
    }

    /**
     * Sets the value of the includeTypes property.
     * 
     */
    public void setIncludeTypes(boolean value) {
        this.includeTypes = value;
    }

    /**
     * Gets the value of the includeProperties property.
     * 
     */
    public boolean isIncludeProperties() {
        return includeProperties;
    }

    /**
     * Sets the value of the includeProperties property.
     * 
     */
    public void setIncludeProperties(boolean value) {
        this.includeProperties = value;
    }

    /**
     * Gets the value of the propertyFilter property.
     * 
     * @return
     *     possible object is
     *     {@link PropertyInfoFilter }
     *     
     */
    public PropertyInfoFilter getPropertyFilter() {
        return propertyFilter;
    }

    /**
     * Sets the value of the propertyFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link PropertyInfoFilter }
     *     
     */
    public void setPropertyFilter(PropertyInfoFilter value) {
        this.propertyFilter = value;
    }

    /**
     * Gets the value of the includeValues property.
     * 
     */
    public boolean isIncludeValues() {
        return includeValues;
    }

    /**
     * Sets the value of the includeValues property.
     * 
     */
    public void setIncludeValues(boolean value) {
        this.includeValues = value;
    }

    /**
     * Gets the value of the includeRelationships property.
     * 
     */
    public boolean isIncludeRelationships() {
        return includeRelationships;
    }

    /**
     * Sets the value of the includeRelationships property.
     * 
     */
    public void setIncludeRelationships(boolean value) {
        this.includeRelationships = value;
    }

    /**
     * Gets the value of the scope property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets the value of the scope property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScope(String value) {
        this.scope = value;
    }

}

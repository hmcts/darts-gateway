
package contextreg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Property complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Property"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="isTransient" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Property", namespace = "http://properties.core.datamodel.fs.documentum.emc.com/")
@XmlSeeAlso({
    StringProperty.class,
    NumberProperty.class,
    DateProperty.class,
    BooleanProperty.class,
    ObjectIdProperty.class,
    ArrayProperty.class,
    RichTextProperty.class
})
public abstract class Property {

    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "isTransient", required = true)
    protected boolean isTransient;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the isTransient property.
     * 
     */
    public boolean isIsTransient() {
        return isTransient;
    }

    /**
     * Sets the value of the isTransient property.
     * 
     */
    public void setIsTransient(boolean value) {
        this.isTransient = value;
    }

}

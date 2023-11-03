
package contextreg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NumberProperty complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NumberProperty"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://properties.core.datamodel.fs.documentum.emc.com/}Property"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="Short" type="{http://www.w3.org/2001/XMLSchema}short"/&gt;
 *           &lt;element name="Integer" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *           &lt;element name="Long" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *           &lt;element name="Double" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NumberProperty", namespace = "http://properties.core.datamodel.fs.documentum.emc.com/", propOrder = {
    "_short",
    "integer",
    "_long",
    "_double"
})
public class NumberProperty
    extends Property
{

    @XmlElement(name = "Short")
    protected Short _short;
    @XmlElement(name = "Integer")
    protected Integer integer;
    @XmlElement(name = "Long")
    protected Long _long;
    @XmlElement(name = "Double")
    protected Double _double;

    /**
     * Gets the value of the short property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getShort() {
        return _short;
    }

    /**
     * Sets the value of the short property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setShort(Short value) {
        this._short = value;
    }

    /**
     * Gets the value of the integer property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getInteger() {
        return integer;
    }

    /**
     * Sets the value of the integer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setInteger(Integer value) {
        this.integer = value;
    }

    /**
     * Gets the value of the long property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getLong() {
        return _long;
    }

    /**
     * Sets the value of the long property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setLong(Long value) {
        this._long = value;
    }

    /**
     * Gets the value of the double property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getDouble() {
        return _double;
    }

    /**
     * Sets the value of the double property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setDouble(Double value) {
        this._double = value;
    }

}

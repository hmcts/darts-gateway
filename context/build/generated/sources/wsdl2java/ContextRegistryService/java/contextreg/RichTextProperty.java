
package contextreg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RichTextProperty complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RichTextProperty"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://properties.core.datamodel.fs.documentum.emc.com/}Property"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Value" type="{http://core.datamodel.fs.documentum.emc.com/}RichText" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RichTextProperty", namespace = "http://properties.core.datamodel.fs.documentum.emc.com/", propOrder = {
    "value"
})
public class RichTextProperty
    extends Property
{

    @XmlElement(name = "Value")
    protected RichText value;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link RichText }
     *     
     */
    public RichText getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link RichText }
     *     
     */
    public void setValue(RichText value) {
        this.value = value;
    }

}

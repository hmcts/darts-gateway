
package contextreg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SsoIdentity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SsoIdentity"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://context.core.datamodel.fs.documentum.emc.com/}BasicIdentity"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="ssoType" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SsoIdentity", namespace = "http://context.core.datamodel.fs.documentum.emc.com/")
public class SsoIdentity
    extends BasicIdentity
{

    @XmlAttribute(name = "ssoType")
    protected String ssoType;

    /**
     * Gets the value of the ssoType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSsoType() {
        return ssoType;
    }

    /**
     * Sets the value of the ssoType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSsoType(String value) {
        this.ssoType = value;
    }

}

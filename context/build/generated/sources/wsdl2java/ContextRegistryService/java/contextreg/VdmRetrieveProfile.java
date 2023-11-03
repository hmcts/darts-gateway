
package contextreg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VdmRetrieveProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VdmRetrieveProfile"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://profiles.core.datamodel.fs.documentum.emc.com/}Profile"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="shouldFollowAssembly" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="binding" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VdmRetrieveProfile")
public class VdmRetrieveProfile
    extends Profile
{

    @XmlAttribute(name = "shouldFollowAssembly", required = true)
    protected boolean shouldFollowAssembly;
    @XmlAttribute(name = "binding")
    protected String binding;

    /**
     * Gets the value of the shouldFollowAssembly property.
     * 
     */
    public boolean isShouldFollowAssembly() {
        return shouldFollowAssembly;
    }

    /**
     * Sets the value of the shouldFollowAssembly property.
     * 
     */
    public void setShouldFollowAssembly(boolean value) {
        this.shouldFollowAssembly = value;
    }

    /**
     * Gets the value of the binding property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBinding() {
        return binding;
    }

    /**
     * Sets the value of the binding property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBinding(String value) {
        this.binding = value;
    }

}

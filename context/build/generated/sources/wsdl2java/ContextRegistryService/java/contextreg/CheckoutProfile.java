
package contextreg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CheckoutProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CheckoutProfile"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://profiles.core.datamodel.fs.documentum.emc.com/}Profile"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="checkoutOnlyVDMRoot" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CheckoutProfile")
public class CheckoutProfile
    extends Profile
{

    @XmlAttribute(name = "checkoutOnlyVDMRoot", required = true)
    protected boolean checkoutOnlyVDMRoot;

    /**
     * Gets the value of the checkoutOnlyVDMRoot property.
     * 
     */
    public boolean isCheckoutOnlyVDMRoot() {
        return checkoutOnlyVDMRoot;
    }

    /**
     * Sets the value of the checkoutOnlyVDMRoot property.
     * 
     */
    public void setCheckoutOnlyVDMRoot(boolean value) {
        this.checkoutOnlyVDMRoot = value;
    }

}

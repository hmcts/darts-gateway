
package contextreg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OverridePermission complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OverridePermission"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="EnableUpdateInDormantState" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OverridePermission", namespace = "http://context.core.datamodel.fs.documentum.emc.com/", propOrder = {
    "enableUpdateInDormantState"
})
public class OverridePermission {

    @XmlElement(name = "EnableUpdateInDormantState")
    protected boolean enableUpdateInDormantState;

    /**
     * Gets the value of the enableUpdateInDormantState property.
     * 
     */
    public boolean isEnableUpdateInDormantState() {
        return enableUpdateInDormantState;
    }

    /**
     * Sets the value of the enableUpdateInDormantState property.
     * 
     */
    public void setEnableUpdateInDormantState(boolean value) {
        this.enableUpdateInDormantState = value;
    }

}

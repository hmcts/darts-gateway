
package contextreg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MoveProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MoveProfile"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://profiles.core.datamodel.fs.documentum.emc.com/}Profile"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="isNonCurrentObjectAllowed" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MoveProfile")
public class MoveProfile
    extends Profile
{

    @XmlAttribute(name = "isNonCurrentObjectAllowed", required = true)
    protected boolean isNonCurrentObjectAllowed;

    /**
     * Gets the value of the isNonCurrentObjectAllowed property.
     * 
     */
    public boolean isIsNonCurrentObjectAllowed() {
        return isNonCurrentObjectAllowed;
    }

    /**
     * Sets the value of the isNonCurrentObjectAllowed property.
     * 
     */
    public void setIsNonCurrentObjectAllowed(boolean value) {
        this.isNonCurrentObjectAllowed = value;
    }

}

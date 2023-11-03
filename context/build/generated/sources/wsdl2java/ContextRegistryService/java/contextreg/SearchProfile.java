
package contextreg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SearchProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SearchProfile"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://profiles.core.datamodel.fs.documentum.emc.com/}Profile"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="isAsyncCall" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchProfile")
public class SearchProfile
    extends Profile
{

    @XmlAttribute(name = "isAsyncCall", required = true)
    protected boolean isAsyncCall;

    /**
     * Gets the value of the isAsyncCall property.
     * 
     */
    public boolean isIsAsyncCall() {
        return isAsyncCall;
    }

    /**
     * Sets the value of the isAsyncCall property.
     * 
     */
    public void setIsAsyncCall(boolean value) {
        this.isAsyncCall = value;
    }

}

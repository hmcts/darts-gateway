
package contextreg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CheckinProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CheckinProfile"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://profiles.core.datamodel.fs.documentum.emc.com/}Profile"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="deleteLocalFileHint" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="makeCurrent" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="checkinOnlyVDMRoot" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CheckinProfile")
public class CheckinProfile
    extends Profile
{

    @XmlAttribute(name = "deleteLocalFileHint", required = true)
    protected boolean deleteLocalFileHint;
    @XmlAttribute(name = "makeCurrent", required = true)
    protected boolean makeCurrent;
    @XmlAttribute(name = "checkinOnlyVDMRoot", required = true)
    protected boolean checkinOnlyVDMRoot;

    /**
     * Gets the value of the deleteLocalFileHint property.
     * 
     */
    public boolean isDeleteLocalFileHint() {
        return deleteLocalFileHint;
    }

    /**
     * Sets the value of the deleteLocalFileHint property.
     * 
     */
    public void setDeleteLocalFileHint(boolean value) {
        this.deleteLocalFileHint = value;
    }

    /**
     * Gets the value of the makeCurrent property.
     * 
     */
    public boolean isMakeCurrent() {
        return makeCurrent;
    }

    /**
     * Sets the value of the makeCurrent property.
     * 
     */
    public void setMakeCurrent(boolean value) {
        this.makeCurrent = value;
    }

    /**
     * Gets the value of the checkinOnlyVDMRoot property.
     * 
     */
    public boolean isCheckinOnlyVDMRoot() {
        return checkinOnlyVDMRoot;
    }

    /**
     * Sets the value of the checkinOnlyVDMRoot property.
     * 
     */
    public void setCheckinOnlyVDMRoot(boolean value) {
        this.checkinOnlyVDMRoot = value;
    }

}

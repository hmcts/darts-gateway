
package contextreg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DeleteProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DeleteProfile"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://profiles.core.datamodel.fs.documentum.emc.com/}Profile"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="isDeepDeleteFolders" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="isDeepDeleteVdmInFolders" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="isPopulateWithReferences" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="isDeepDeleteChildrenInFolders" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="isDeleteVdm" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="versionStrategy" type="{http://profiles.core.datamodel.fs.documentum.emc.com/}DeleteVersionStrategy" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeleteProfile")
public class DeleteProfile
    extends Profile
{

    @XmlAttribute(name = "isDeepDeleteFolders", required = true)
    protected boolean isDeepDeleteFolders;
    @XmlAttribute(name = "isDeepDeleteVdmInFolders", required = true)
    protected boolean isDeepDeleteVdmInFolders;
    @XmlAttribute(name = "isPopulateWithReferences", required = true)
    protected boolean isPopulateWithReferences;
    @XmlAttribute(name = "isDeepDeleteChildrenInFolders", required = true)
    protected boolean isDeepDeleteChildrenInFolders;
    @XmlAttribute(name = "isDeleteVdm", required = true)
    protected boolean isDeleteVdm;
    @XmlAttribute(name = "versionStrategy")
    protected DeleteVersionStrategy versionStrategy;

    /**
     * Gets the value of the isDeepDeleteFolders property.
     * 
     */
    public boolean isIsDeepDeleteFolders() {
        return isDeepDeleteFolders;
    }

    /**
     * Sets the value of the isDeepDeleteFolders property.
     * 
     */
    public void setIsDeepDeleteFolders(boolean value) {
        this.isDeepDeleteFolders = value;
    }

    /**
     * Gets the value of the isDeepDeleteVdmInFolders property.
     * 
     */
    public boolean isIsDeepDeleteVdmInFolders() {
        return isDeepDeleteVdmInFolders;
    }

    /**
     * Sets the value of the isDeepDeleteVdmInFolders property.
     * 
     */
    public void setIsDeepDeleteVdmInFolders(boolean value) {
        this.isDeepDeleteVdmInFolders = value;
    }

    /**
     * Gets the value of the isPopulateWithReferences property.
     * 
     */
    public boolean isIsPopulateWithReferences() {
        return isPopulateWithReferences;
    }

    /**
     * Sets the value of the isPopulateWithReferences property.
     * 
     */
    public void setIsPopulateWithReferences(boolean value) {
        this.isPopulateWithReferences = value;
    }

    /**
     * Gets the value of the isDeepDeleteChildrenInFolders property.
     * 
     */
    public boolean isIsDeepDeleteChildrenInFolders() {
        return isDeepDeleteChildrenInFolders;
    }

    /**
     * Sets the value of the isDeepDeleteChildrenInFolders property.
     * 
     */
    public void setIsDeepDeleteChildrenInFolders(boolean value) {
        this.isDeepDeleteChildrenInFolders = value;
    }

    /**
     * Gets the value of the isDeleteVdm property.
     * 
     */
    public boolean isIsDeleteVdm() {
        return isDeleteVdm;
    }

    /**
     * Sets the value of the isDeleteVdm property.
     * 
     */
    public void setIsDeleteVdm(boolean value) {
        this.isDeleteVdm = value;
    }

    /**
     * Gets the value of the versionStrategy property.
     * 
     * @return
     *     possible object is
     *     {@link DeleteVersionStrategy }
     *     
     */
    public DeleteVersionStrategy getVersionStrategy() {
        return versionStrategy;
    }

    /**
     * Sets the value of the versionStrategy property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeleteVersionStrategy }
     *     
     */
    public void setVersionStrategy(DeleteVersionStrategy value) {
        this.versionStrategy = value;
    }

}

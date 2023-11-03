
package contextreg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PermissionProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PermissionProfile"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://profiles.core.datamodel.fs.documentum.emc.com/}Profile"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="permissionTypeFilter" type="{http://profiles.core.datamodel.fs.documentum.emc.com/}PermissionTypeFilter" /&gt;
 *       &lt;attribute name="permissionType" type="{http://core.datamodel.fs.documentum.emc.com/}PermissionType" /&gt;
 *       &lt;attribute name="isUseCompoundPermissions" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PermissionProfile")
public class PermissionProfile
    extends Profile
{

    @XmlAttribute(name = "permissionTypeFilter")
    protected PermissionTypeFilter permissionTypeFilter;
    @XmlAttribute(name = "permissionType")
    protected PermissionType permissionType;
    @XmlAttribute(name = "isUseCompoundPermissions", required = true)
    protected boolean isUseCompoundPermissions;

    /**
     * Gets the value of the permissionTypeFilter property.
     * 
     * @return
     *     possible object is
     *     {@link PermissionTypeFilter }
     *     
     */
    public PermissionTypeFilter getPermissionTypeFilter() {
        return permissionTypeFilter;
    }

    /**
     * Sets the value of the permissionTypeFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link PermissionTypeFilter }
     *     
     */
    public void setPermissionTypeFilter(PermissionTypeFilter value) {
        this.permissionTypeFilter = value;
    }

    /**
     * Gets the value of the permissionType property.
     * 
     * @return
     *     possible object is
     *     {@link PermissionType }
     *     
     */
    public PermissionType getPermissionType() {
        return permissionType;
    }

    /**
     * Sets the value of the permissionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link PermissionType }
     *     
     */
    public void setPermissionType(PermissionType value) {
        this.permissionType = value;
    }

    /**
     * Gets the value of the isUseCompoundPermissions property.
     * 
     */
    public boolean isIsUseCompoundPermissions() {
        return isUseCompoundPermissions;
    }

    /**
     * Sets the value of the isUseCompoundPermissions property.
     * 
     */
    public void setIsUseCompoundPermissions(boolean value) {
        this.isUseCompoundPermissions = value;
    }

}

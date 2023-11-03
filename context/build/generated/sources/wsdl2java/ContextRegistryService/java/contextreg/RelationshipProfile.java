
package contextreg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RelationshipProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RelationshipProfile"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://profiles.core.datamodel.fs.documentum.emc.com/}Profile"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PropertyProfile" type="{http://profiles.core.datamodel.fs.documentum.emc.com/}PropertyProfile" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="resultDataMode" type="{http://profiles.core.datamodel.fs.documentum.emc.com/}ResultDataMode" /&gt;
 *       &lt;attribute name="nameFilter" type="{http://profiles.core.datamodel.fs.documentum.emc.com/}RelationshipNameFilter" /&gt;
 *       &lt;attribute name="relationName" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="targetRoleFilter" type="{http://profiles.core.datamodel.fs.documentum.emc.com/}TargetRoleFilter" /&gt;
 *       &lt;attribute name="targetRole" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="depthFilter" type="{http://profiles.core.datamodel.fs.documentum.emc.com/}DepthFilter" /&gt;
 *       &lt;attribute name="depth" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RelationshipProfile", propOrder = {
    "propertyProfile"
})
public class RelationshipProfile
    extends Profile
{

    @XmlElement(name = "PropertyProfile")
    protected PropertyProfile propertyProfile;
    @XmlAttribute(name = "resultDataMode")
    protected ResultDataMode resultDataMode;
    @XmlAttribute(name = "nameFilter")
    protected RelationshipNameFilter nameFilter;
    @XmlAttribute(name = "relationName")
    protected String relationName;
    @XmlAttribute(name = "targetRoleFilter")
    protected TargetRoleFilter targetRoleFilter;
    @XmlAttribute(name = "targetRole")
    protected String targetRole;
    @XmlAttribute(name = "depthFilter")
    protected DepthFilter depthFilter;
    @XmlAttribute(name = "depth", required = true)
    protected int depth;

    /**
     * Gets the value of the propertyProfile property.
     * 
     * @return
     *     possible object is
     *     {@link PropertyProfile }
     *     
     */
    public PropertyProfile getPropertyProfile() {
        return propertyProfile;
    }

    /**
     * Sets the value of the propertyProfile property.
     * 
     * @param value
     *     allowed object is
     *     {@link PropertyProfile }
     *     
     */
    public void setPropertyProfile(PropertyProfile value) {
        this.propertyProfile = value;
    }

    /**
     * Gets the value of the resultDataMode property.
     * 
     * @return
     *     possible object is
     *     {@link ResultDataMode }
     *     
     */
    public ResultDataMode getResultDataMode() {
        return resultDataMode;
    }

    /**
     * Sets the value of the resultDataMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultDataMode }
     *     
     */
    public void setResultDataMode(ResultDataMode value) {
        this.resultDataMode = value;
    }

    /**
     * Gets the value of the nameFilter property.
     * 
     * @return
     *     possible object is
     *     {@link RelationshipNameFilter }
     *     
     */
    public RelationshipNameFilter getNameFilter() {
        return nameFilter;
    }

    /**
     * Sets the value of the nameFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link RelationshipNameFilter }
     *     
     */
    public void setNameFilter(RelationshipNameFilter value) {
        this.nameFilter = value;
    }

    /**
     * Gets the value of the relationName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelationName() {
        return relationName;
    }

    /**
     * Sets the value of the relationName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelationName(String value) {
        this.relationName = value;
    }

    /**
     * Gets the value of the targetRoleFilter property.
     * 
     * @return
     *     possible object is
     *     {@link TargetRoleFilter }
     *     
     */
    public TargetRoleFilter getTargetRoleFilter() {
        return targetRoleFilter;
    }

    /**
     * Sets the value of the targetRoleFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link TargetRoleFilter }
     *     
     */
    public void setTargetRoleFilter(TargetRoleFilter value) {
        this.targetRoleFilter = value;
    }

    /**
     * Gets the value of the targetRole property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetRole() {
        return targetRole;
    }

    /**
     * Sets the value of the targetRole property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetRole(String value) {
        this.targetRole = value;
    }

    /**
     * Gets the value of the depthFilter property.
     * 
     * @return
     *     possible object is
     *     {@link DepthFilter }
     *     
     */
    public DepthFilter getDepthFilter() {
        return depthFilter;
    }

    /**
     * Sets the value of the depthFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link DepthFilter }
     *     
     */
    public void setDepthFilter(DepthFilter value) {
        this.depthFilter = value;
    }

    /**
     * Gets the value of the depth property.
     * 
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Sets the value of the depth property.
     * 
     */
    public void setDepth(int value) {
        this.depth = value;
    }

}


package contextreg;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VdmUpdateProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VdmUpdateProfile"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://profiles.core.datamodel.fs.documentum.emc.com/}Profile"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Labels" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="versionStrategy" type="{http://core.datamodel.fs.documentum.emc.com/}VersionStrategy" /&gt;
 *       &lt;attribute name="retainLock" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="updateMethod" type="{http://profiles.core.datamodel.fs.documentum.emc.com/}ListUpdateMethod" /&gt;
 *       &lt;attribute name="convertToSimple" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VdmUpdateProfile", propOrder = {
    "labels"
})
public class VdmUpdateProfile
    extends Profile
{

    @XmlElement(name = "Labels")
    protected List<String> labels;
    @XmlAttribute(name = "versionStrategy")
    protected VersionStrategy versionStrategy;
    @XmlAttribute(name = "retainLock", required = true)
    protected boolean retainLock;
    @XmlAttribute(name = "updateMethod")
    protected ListUpdateMethod updateMethod;
    @XmlAttribute(name = "convertToSimple", required = true)
    protected boolean convertToSimple;

    /**
     * Gets the value of the labels property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the labels property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLabels().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getLabels() {
        if (labels == null) {
            labels = new ArrayList<String>();
        }
        return this.labels;
    }

    /**
     * Gets the value of the versionStrategy property.
     * 
     * @return
     *     possible object is
     *     {@link VersionStrategy }
     *     
     */
    public VersionStrategy getVersionStrategy() {
        return versionStrategy;
    }

    /**
     * Sets the value of the versionStrategy property.
     * 
     * @param value
     *     allowed object is
     *     {@link VersionStrategy }
     *     
     */
    public void setVersionStrategy(VersionStrategy value) {
        this.versionStrategy = value;
    }

    /**
     * Gets the value of the retainLock property.
     * 
     */
    public boolean isRetainLock() {
        return retainLock;
    }

    /**
     * Sets the value of the retainLock property.
     * 
     */
    public void setRetainLock(boolean value) {
        this.retainLock = value;
    }

    /**
     * Gets the value of the updateMethod property.
     * 
     * @return
     *     possible object is
     *     {@link ListUpdateMethod }
     *     
     */
    public ListUpdateMethod getUpdateMethod() {
        return updateMethod;
    }

    /**
     * Sets the value of the updateMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListUpdateMethod }
     *     
     */
    public void setUpdateMethod(ListUpdateMethod value) {
        this.updateMethod = value;
    }

    /**
     * Gets the value of the convertToSimple property.
     * 
     */
    public boolean isConvertToSimple() {
        return convertToSimple;
    }

    /**
     * Sets the value of the convertToSimple property.
     * 
     */
    public void setConvertToSimple(boolean value) {
        this.convertToSimple = value;
    }

}

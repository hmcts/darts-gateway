
package contextreg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UcfContent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UcfContent"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://content.core.datamodel.fs.documentum.emc.com/}Content"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="LocalFilePath" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ActivityInfo" type="{http://content.core.datamodel.fs.documentum.emc.com/}ActivityInfo" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UcfContent", namespace = "http://content.core.datamodel.fs.documentum.emc.com/", propOrder = {
    "localFilePath",
    "activityInfo"
})
public class UcfContent
    extends Content
{

    @XmlElement(name = "LocalFilePath", required = true)
    protected String localFilePath;
    @XmlElement(name = "ActivityInfo")
    protected ActivityInfo activityInfo;

    /**
     * Gets the value of the localFilePath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocalFilePath() {
        return localFilePath;
    }

    /**
     * Sets the value of the localFilePath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocalFilePath(String value) {
        this.localFilePath = value;
    }

    /**
     * Gets the value of the activityInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ActivityInfo }
     *     
     */
    public ActivityInfo getActivityInfo() {
        return activityInfo;
    }

    /**
     * Sets the value of the activityInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActivityInfo }
     *     
     */
    public void setActivityInfo(ActivityInfo value) {
        this.activityInfo = value;
    }

}


package contextreg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RichTextProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RichTextProfile"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://profiles.core.datamodel.fs.documentum.emc.com/}Profile"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ImageUrlTemplate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AnchorUrlTemplate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="transferContent" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RichTextProfile", propOrder = {
    "imageUrlTemplate",
    "anchorUrlTemplate"
})
public class RichTextProfile
    extends Profile
{

    @XmlElement(name = "ImageUrlTemplate")
    protected String imageUrlTemplate;
    @XmlElement(name = "AnchorUrlTemplate")
    protected String anchorUrlTemplate;
    @XmlAttribute(name = "transferContent", required = true)
    protected boolean transferContent;

    /**
     * Gets the value of the imageUrlTemplate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImageUrlTemplate() {
        return imageUrlTemplate;
    }

    /**
     * Sets the value of the imageUrlTemplate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImageUrlTemplate(String value) {
        this.imageUrlTemplate = value;
    }

    /**
     * Gets the value of the anchorUrlTemplate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnchorUrlTemplate() {
        return anchorUrlTemplate;
    }

    /**
     * Sets the value of the anchorUrlTemplate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnchorUrlTemplate(String value) {
        this.anchorUrlTemplate = value;
    }

    /**
     * Gets the value of the transferContent property.
     * 
     */
    public boolean isTransferContent() {
        return transferContent;
    }

    /**
     * Sets the value of the transferContent property.
     * 
     */
    public void setTransferContent(boolean value) {
        this.transferContent = value;
    }

}

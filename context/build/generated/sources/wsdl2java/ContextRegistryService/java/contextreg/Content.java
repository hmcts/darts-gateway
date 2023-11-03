
package contextreg;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Content complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Content"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="renditionType" type="{http://content.core.datamodel.fs.documentum.emc.com/}RenditionType" minOccurs="0"/&gt;
 *         &lt;element name="intentModifier" type="{http://content.core.datamodel.fs.documentum.emc.com/}ContentIntentModifier" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="format" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="pageNumber" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="pageModifier" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="contentTransferMode" type="{http://content.core.datamodel.fs.documentum.emc.com/}ContentTransferMode" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Content", namespace = "http://content.core.datamodel.fs.documentum.emc.com/", propOrder = {
    "renditionType",
    "intentModifier"
})
@XmlSeeAlso({
    BinaryContent.class,
    DataHandlerContent.class,
    UcfContent.class,
    UrlContent.class
})
public abstract class Content {

    @XmlElementRef(name = "renditionType", namespace = "http://content.core.datamodel.fs.documentum.emc.com/", type = JAXBElement.class, required = false)
    protected JAXBElement<RenditionType> renditionType;
    @XmlElementRef(name = "intentModifier", namespace = "http://content.core.datamodel.fs.documentum.emc.com/", type = JAXBElement.class, required = false)
    protected JAXBElement<ContentIntentModifier> intentModifier;
    @XmlAttribute(name = "format", required = true)
    protected String format;
    @XmlAttribute(name = "pageNumber", required = true)
    protected int pageNumber;
    @XmlAttribute(name = "pageModifier")
    protected String pageModifier;
    @XmlAttribute(name = "contentTransferMode")
    protected ContentTransferMode contentTransferMode;

    /**
     * Gets the value of the renditionType property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link RenditionType }{@code >}
     *     
     */
    public JAXBElement<RenditionType> getRenditionType() {
        return renditionType;
    }

    /**
     * Sets the value of the renditionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link RenditionType }{@code >}
     *     
     */
    public void setRenditionType(JAXBElement<RenditionType> value) {
        this.renditionType = value;
    }

    /**
     * Gets the value of the intentModifier property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ContentIntentModifier }{@code >}
     *     
     */
    public JAXBElement<ContentIntentModifier> getIntentModifier() {
        return intentModifier;
    }

    /**
     * Sets the value of the intentModifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ContentIntentModifier }{@code >}
     *     
     */
    public void setIntentModifier(JAXBElement<ContentIntentModifier> value) {
        this.intentModifier = value;
    }

    /**
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormat(String value) {
        this.format = value;
    }

    /**
     * Gets the value of the pageNumber property.
     * 
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Sets the value of the pageNumber property.
     * 
     */
    public void setPageNumber(int value) {
        this.pageNumber = value;
    }

    /**
     * Gets the value of the pageModifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPageModifier() {
        return pageModifier;
    }

    /**
     * Sets the value of the pageModifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPageModifier(String value) {
        this.pageModifier = value;
    }

    /**
     * Gets the value of the contentTransferMode property.
     * 
     * @return
     *     possible object is
     *     {@link ContentTransferMode }
     *     
     */
    public ContentTransferMode getContentTransferMode() {
        return contentTransferMode;
    }

    /**
     * Sets the value of the contentTransferMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContentTransferMode }
     *     
     */
    public void setContentTransferMode(ContentTransferMode value) {
        this.contentTransferMode = value;
    }

}

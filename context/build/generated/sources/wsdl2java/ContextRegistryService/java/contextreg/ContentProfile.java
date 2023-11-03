
package contextreg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContentProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContentProfile"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://profiles.core.datamodel.fs.documentum.emc.com/}Profile"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="formatFilter" type="{http://profiles.core.datamodel.fs.documentum.emc.com/}FormatFilter" /&gt;
 *       &lt;attribute name="format" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="pageFilter" type="{http://profiles.core.datamodel.fs.documentum.emc.com/}PageFilter" /&gt;
 *       &lt;attribute name="pageNumber" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="pageModifierFilter" type="{http://profiles.core.datamodel.fs.documentum.emc.com/}PageModifierFilter" /&gt;
 *       &lt;attribute name="pageModifier" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="renditionTypeFilter" type="{http://profiles.core.datamodel.fs.documentum.emc.com/}RenditionTypeFilter" /&gt;
 *       &lt;attribute name="renditionOption" type="{http://profiles.core.datamodel.fs.documentum.emc.com/}RenditionOption" /&gt;
 *       &lt;attribute name="contentReturnType" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="postTransferAction" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="urlReturnPolicy" type="{http://profiles.core.datamodel.fs.documentum.emc.com/}UrlReturnPolicy" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContentProfile")
public class ContentProfile
    extends Profile
{

    @XmlAttribute(name = "formatFilter")
    protected FormatFilter formatFilter;
    @XmlAttribute(name = "format")
    protected String format;
    @XmlAttribute(name = "pageFilter")
    protected PageFilter pageFilter;
    @XmlAttribute(name = "pageNumber", required = true)
    protected int pageNumber;
    @XmlAttribute(name = "pageModifierFilter")
    protected PageModifierFilter pageModifierFilter;
    @XmlAttribute(name = "pageModifier")
    protected String pageModifier;
    @XmlAttribute(name = "renditionTypeFilter")
    protected RenditionTypeFilter renditionTypeFilter;
    @XmlAttribute(name = "renditionOption")
    protected RenditionOption renditionOption;
    @XmlAttribute(name = "contentReturnType")
    protected String contentReturnType;
    @XmlAttribute(name = "postTransferAction")
    protected String postTransferAction;
    @XmlAttribute(name = "urlReturnPolicy")
    protected UrlReturnPolicy urlReturnPolicy;

    /**
     * Gets the value of the formatFilter property.
     * 
     * @return
     *     possible object is
     *     {@link FormatFilter }
     *     
     */
    public FormatFilter getFormatFilter() {
        return formatFilter;
    }

    /**
     * Sets the value of the formatFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link FormatFilter }
     *     
     */
    public void setFormatFilter(FormatFilter value) {
        this.formatFilter = value;
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
     * Gets the value of the pageFilter property.
     * 
     * @return
     *     possible object is
     *     {@link PageFilter }
     *     
     */
    public PageFilter getPageFilter() {
        return pageFilter;
    }

    /**
     * Sets the value of the pageFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link PageFilter }
     *     
     */
    public void setPageFilter(PageFilter value) {
        this.pageFilter = value;
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
     * Gets the value of the pageModifierFilter property.
     * 
     * @return
     *     possible object is
     *     {@link PageModifierFilter }
     *     
     */
    public PageModifierFilter getPageModifierFilter() {
        return pageModifierFilter;
    }

    /**
     * Sets the value of the pageModifierFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link PageModifierFilter }
     *     
     */
    public void setPageModifierFilter(PageModifierFilter value) {
        this.pageModifierFilter = value;
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
     * Gets the value of the renditionTypeFilter property.
     * 
     * @return
     *     possible object is
     *     {@link RenditionTypeFilter }
     *     
     */
    public RenditionTypeFilter getRenditionTypeFilter() {
        return renditionTypeFilter;
    }

    /**
     * Sets the value of the renditionTypeFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link RenditionTypeFilter }
     *     
     */
    public void setRenditionTypeFilter(RenditionTypeFilter value) {
        this.renditionTypeFilter = value;
    }

    /**
     * Gets the value of the renditionOption property.
     * 
     * @return
     *     possible object is
     *     {@link RenditionOption }
     *     
     */
    public RenditionOption getRenditionOption() {
        return renditionOption;
    }

    /**
     * Sets the value of the renditionOption property.
     * 
     * @param value
     *     allowed object is
     *     {@link RenditionOption }
     *     
     */
    public void setRenditionOption(RenditionOption value) {
        this.renditionOption = value;
    }

    /**
     * Gets the value of the contentReturnType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContentReturnType() {
        return contentReturnType;
    }

    /**
     * Sets the value of the contentReturnType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContentReturnType(String value) {
        this.contentReturnType = value;
    }

    /**
     * Gets the value of the postTransferAction property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostTransferAction() {
        return postTransferAction;
    }

    /**
     * Sets the value of the postTransferAction property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostTransferAction(String value) {
        this.postTransferAction = value;
    }

    /**
     * Gets the value of the urlReturnPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link UrlReturnPolicy }
     *     
     */
    public UrlReturnPolicy getUrlReturnPolicy() {
        return urlReturnPolicy;
    }

    /**
     * Sets the value of the urlReturnPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link UrlReturnPolicy }
     *     
     */
    public void setUrlReturnPolicy(UrlReturnPolicy value) {
        this.urlReturnPolicy = value;
    }

}

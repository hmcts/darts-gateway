
package contextreg;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServiceContext complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceContext"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Identities" type="{http://context.core.datamodel.fs.documentum.emc.com/}Identity" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Profiles" type="{http://profiles.core.datamodel.fs.documentum.emc.com/}Profile" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="RuntimeProperties" type="{http://properties.core.datamodel.fs.documentum.emc.com/}PropertySet" minOccurs="0"/&gt;
 *         &lt;element name="OverridePermission" type="{http://context.core.datamodel.fs.documentum.emc.com/}OverridePermission" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="token" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="locale" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceContext", namespace = "http://context.core.datamodel.fs.documentum.emc.com/", propOrder = {
    "identities",
    "profiles",
    "runtimeProperties",
    "overridePermission"
})
public class ServiceContext {

    @XmlElement(name = "Identities")
    protected List<Identity> identities;
    @XmlElement(name = "Profiles")
    protected List<Profile> profiles;
    @XmlElement(name = "RuntimeProperties")
    protected PropertySet runtimeProperties;
    @XmlElement(name = "OverridePermission")
    protected OverridePermission overridePermission;
    @XmlAttribute(name = "token")
    protected String token;
    @XmlAttribute(name = "locale")
    protected String locale;

    /**
     * Gets the value of the identities property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the identities property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIdentities().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Identity }
     * 
     * 
     */
    public List<Identity> getIdentities() {
        if (identities == null) {
            identities = new ArrayList<Identity>();
        }
        return this.identities;
    }

    /**
     * Gets the value of the profiles property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the profiles property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProfiles().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Profile }
     * 
     * 
     */
    public List<Profile> getProfiles() {
        if (profiles == null) {
            profiles = new ArrayList<Profile>();
        }
        return this.profiles;
    }

    /**
     * Gets the value of the runtimeProperties property.
     * 
     * @return
     *     possible object is
     *     {@link PropertySet }
     *     
     */
    public PropertySet getRuntimeProperties() {
        return runtimeProperties;
    }

    /**
     * Sets the value of the runtimeProperties property.
     * 
     * @param value
     *     allowed object is
     *     {@link PropertySet }
     *     
     */
    public void setRuntimeProperties(PropertySet value) {
        this.runtimeProperties = value;
    }

    /**
     * Gets the value of the overridePermission property.
     * 
     * @return
     *     possible object is
     *     {@link OverridePermission }
     *     
     */
    public OverridePermission getOverridePermission() {
        return overridePermission;
    }

    /**
     * Sets the value of the overridePermission property.
     * 
     * @param value
     *     allowed object is
     *     {@link OverridePermission }
     *     
     */
    public void setOverridePermission(OverridePermission value) {
        this.overridePermission = value;
    }

    /**
     * Gets the value of the token property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the value of the token property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToken(String value) {
        this.token = value;
    }

    /**
     * Gets the value of the locale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Sets the value of the locale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocale(String value) {
        this.locale = value;
    }

}

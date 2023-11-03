
package contextreg;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClusteringStrategy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClusteringStrategy"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://query.core.datamodel.fs.documentum.emc.com/}GroupingStrategy"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="attributes" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="tokenizers" type="{http://properties.core.datamodel.fs.documentum.emc.com/}PropertySet" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="clusteringRange" type="{http://query.core.datamodel.fs.documentum.emc.com/}ClusteringRange" /&gt;
 *       &lt;attribute name="clusteringThreshold" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="strategyName" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="returnIdentitySet" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClusteringStrategy", namespace = "http://query.core.datamodel.fs.documentum.emc.com/", propOrder = {
    "attributes",
    "tokenizers"
})
public class ClusteringStrategy
    extends GroupingStrategy
{

    protected List<String> attributes;
    protected PropertySet tokenizers;
    @XmlAttribute(name = "clusteringRange")
    protected ClusteringRange clusteringRange;
    @XmlAttribute(name = "clusteringThreshold", required = true)
    protected int clusteringThreshold;
    @XmlAttribute(name = "strategyName")
    protected String strategyName;
    @XmlAttribute(name = "returnIdentitySet", required = true)
    protected boolean returnIdentitySet;

    /**
     * Gets the value of the attributes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the attributes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttributes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAttributes() {
        if (attributes == null) {
            attributes = new ArrayList<String>();
        }
        return this.attributes;
    }

    /**
     * Gets the value of the tokenizers property.
     * 
     * @return
     *     possible object is
     *     {@link PropertySet }
     *     
     */
    public PropertySet getTokenizers() {
        return tokenizers;
    }

    /**
     * Sets the value of the tokenizers property.
     * 
     * @param value
     *     allowed object is
     *     {@link PropertySet }
     *     
     */
    public void setTokenizers(PropertySet value) {
        this.tokenizers = value;
    }

    /**
     * Gets the value of the clusteringRange property.
     * 
     * @return
     *     possible object is
     *     {@link ClusteringRange }
     *     
     */
    public ClusteringRange getClusteringRange() {
        return clusteringRange;
    }

    /**
     * Sets the value of the clusteringRange property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClusteringRange }
     *     
     */
    public void setClusteringRange(ClusteringRange value) {
        this.clusteringRange = value;
    }

    /**
     * Gets the value of the clusteringThreshold property.
     * 
     */
    public int getClusteringThreshold() {
        return clusteringThreshold;
    }

    /**
     * Sets the value of the clusteringThreshold property.
     * 
     */
    public void setClusteringThreshold(int value) {
        this.clusteringThreshold = value;
    }

    /**
     * Gets the value of the strategyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStrategyName() {
        return strategyName;
    }

    /**
     * Sets the value of the strategyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStrategyName(String value) {
        this.strategyName = value;
    }

    /**
     * Gets the value of the returnIdentitySet property.
     * 
     */
    public boolean isReturnIdentitySet() {
        return returnIdentitySet;
    }

    /**
     * Sets the value of the returnIdentitySet property.
     * 
     */
    public void setReturnIdentitySet(boolean value) {
        this.returnIdentitySet = value;
    }

}

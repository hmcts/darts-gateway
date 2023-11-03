
package contextreg;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClusteringProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClusteringProfile"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://profiles.core.datamodel.fs.documentum.emc.com/}Profile"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ClusteringStrategies" type="{http://query.core.datamodel.fs.documentum.emc.com/}ClusteringStrategy" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClusteringProfile", propOrder = {
    "clusteringStrategies"
})
public class ClusteringProfile
    extends Profile
{

    @XmlElement(name = "ClusteringStrategies")
    protected List<ClusteringStrategy> clusteringStrategies;

    /**
     * Gets the value of the clusteringStrategies property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the clusteringStrategies property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClusteringStrategies().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClusteringStrategy }
     * 
     * 
     */
    public List<ClusteringStrategy> getClusteringStrategies() {
        if (clusteringStrategies == null) {
            clusteringStrategies = new ArrayList<ClusteringStrategy>();
        }
        return this.clusteringStrategies;
    }

}

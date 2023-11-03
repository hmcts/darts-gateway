
package contextreg;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ObjectIdArrayProperty complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ObjectIdArrayProperty"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://properties.core.datamodel.fs.documentum.emc.com/}ArrayProperty"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Values" type="{http://core.datamodel.fs.documentum.emc.com/}ObjectId" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectIdArrayProperty", namespace = "http://properties.core.datamodel.fs.documentum.emc.com/", propOrder = {
    "values"
})
public class ObjectIdArrayProperty
    extends ArrayProperty
{

    @XmlElement(name = "Values", nillable = true)
    protected List<ObjectId> values;

    /**
     * Gets the value of the values property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the values property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValues().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ObjectId }
     * 
     * 
     */
    public List<ObjectId> getValues() {
        if (values == null) {
            values = new ArrayList<ObjectId>();
        }
        return this.values;
    }

}

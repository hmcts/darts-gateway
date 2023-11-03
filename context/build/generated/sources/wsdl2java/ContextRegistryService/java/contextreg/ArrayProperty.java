
package contextreg;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayProperty complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayProperty"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://properties.core.datamodel.fs.documentum.emc.com/}Property"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ValueActions" type="{http://properties.core.datamodel.fs.documentum.emc.com/}ValueAction" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayProperty", namespace = "http://properties.core.datamodel.fs.documentum.emc.com/", propOrder = {
    "valueActions"
})
@XmlSeeAlso({
    StringArrayProperty.class,
    NumberArrayProperty.class,
    BooleanArrayProperty.class,
    DateArrayProperty.class,
    ObjectIdArrayProperty.class
})
public abstract class ArrayProperty
    extends Property
{

    @XmlElement(name = "ValueActions")
    protected List<ValueAction> valueActions;

    /**
     * Gets the value of the valueActions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the valueActions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValueActions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ValueAction }
     * 
     * 
     */
    public List<ValueAction> getValueActions() {
        if (valueActions == null) {
            valueActions = new ArrayList<ValueAction>();
        }
        return this.valueActions;
    }

}


package contextreg;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NumberArrayProperty complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NumberArrayProperty"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://properties.core.datamodel.fs.documentum.emc.com/}ArrayProperty"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;element name="Short" type="{http://www.w3.org/2001/XMLSchema}short"/&gt;
 *           &lt;element name="Integer" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *           &lt;element name="Long" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *           &lt;element name="Double" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NumberArrayProperty", namespace = "http://properties.core.datamodel.fs.documentum.emc.com/", propOrder = {
    "shortOrIntegerOrLong"
})
public class NumberArrayProperty
    extends ArrayProperty
{

    @XmlElements({
        @XmlElement(name = "Short", type = Short.class, nillable = true),
        @XmlElement(name = "Integer", type = Integer.class, nillable = true),
        @XmlElement(name = "Long", type = Long.class, nillable = true),
        @XmlElement(name = "Double", type = Double.class, nillable = true)
    })
    protected List<Comparable> shortOrIntegerOrLong;

    /**
     * Gets the value of the shortOrIntegerOrLong property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the shortOrIntegerOrLong property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getShortOrIntegerOrLong().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     * {@link Integer }
     * {@link Long }
     * {@link Short }
     * 
     * 
     */
    public List<Comparable> getShortOrIntegerOrLong() {
        if (shortOrIntegerOrLong == null) {
            shortOrIntegerOrLong = new ArrayList<Comparable>();
        }
        return this.shortOrIntegerOrLong;
    }

}

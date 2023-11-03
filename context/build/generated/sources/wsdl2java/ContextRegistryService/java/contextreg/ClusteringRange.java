
package contextreg;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClusteringRange.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="ClusteringRange"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="LOW"/&gt;
 *     &lt;enumeration value="MEDIUM"/&gt;
 *     &lt;enumeration value="HIGH"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ClusteringRange", namespace = "http://query.core.datamodel.fs.documentum.emc.com/")
@XmlEnum
public enum ClusteringRange {

    LOW,
    MEDIUM,
    HIGH;

    public String value() {
        return name();
    }

    public static ClusteringRange fromValue(String v) {
        return valueOf(v);
    }

}

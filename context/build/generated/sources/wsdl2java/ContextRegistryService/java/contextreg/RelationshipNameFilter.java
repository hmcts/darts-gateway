
package contextreg;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RelationshipNameFilter.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="RelationshipNameFilter"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="NONE"/&gt;
 *     &lt;enumeration value="SPECIFIED"/&gt;
 *     &lt;enumeration value="ANY"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "RelationshipNameFilter")
@XmlEnum
public enum RelationshipNameFilter {

    NONE,
    SPECIFIED,
    ANY;

    public String value() {
        return name();
    }

    public static RelationshipNameFilter fromValue(String v) {
        return valueOf(v);
    }

}

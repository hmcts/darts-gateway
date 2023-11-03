
package contextreg;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PropertyFilterMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="PropertyFilterMode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="NONE"/&gt;
 *     &lt;enumeration value="IMPLIED"/&gt;
 *     &lt;enumeration value="SPECIFIED_BY_INCLUDE"/&gt;
 *     &lt;enumeration value="SPECIFIED_BY_EXCLUDE"/&gt;
 *     &lt;enumeration value="ALL_NON_SYSTEM"/&gt;
 *     &lt;enumeration value="ALL"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "PropertyFilterMode")
@XmlEnum
public enum PropertyFilterMode {

    NONE,
    IMPLIED,
    SPECIFIED_BY_INCLUDE,
    SPECIFIED_BY_EXCLUDE,
    ALL_NON_SYSTEM,
    ALL;

    public String value() {
        return name();
    }

    public static PropertyFilterMode fromValue(String v) {
        return valueOf(v);
    }

}

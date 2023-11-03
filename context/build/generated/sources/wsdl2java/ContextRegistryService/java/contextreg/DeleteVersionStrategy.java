
package contextreg;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DeleteVersionStrategy.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="DeleteVersionStrategy"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="IMPLIED"/&gt;
 *     &lt;enumeration value="SELECTED_VERSIONS"/&gt;
 *     &lt;enumeration value="UNUSED_VERSIONS"/&gt;
 *     &lt;enumeration value="ALL_VERSIONS"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "DeleteVersionStrategy")
@XmlEnum
public enum DeleteVersionStrategy {

    IMPLIED,
    SELECTED_VERSIONS,
    UNUSED_VERSIONS,
    ALL_VERSIONS;

    public String value() {
        return name();
    }

    public static DeleteVersionStrategy fromValue(String v) {
        return valueOf(v);
    }

}

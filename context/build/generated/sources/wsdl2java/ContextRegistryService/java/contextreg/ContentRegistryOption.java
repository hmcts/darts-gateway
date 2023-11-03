
package contextreg;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContentRegistryOption.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="ContentRegistryOption"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="IMPLIED"/&gt;
 *     &lt;enumeration value="UNREGISTERED"/&gt;
 *     &lt;enumeration value="REGISTERED_AS_VIEWED"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ContentRegistryOption")
@XmlEnum
public enum ContentRegistryOption {

    IMPLIED,
    UNREGISTERED,
    REGISTERED_AS_VIEWED;

    public String value() {
        return name();
    }

    public static ContentRegistryOption fromValue(String v) {
        return valueOf(v);
    }

}

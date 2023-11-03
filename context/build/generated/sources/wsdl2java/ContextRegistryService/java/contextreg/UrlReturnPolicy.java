
package contextreg;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UrlReturnPolicy.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="UrlReturnPolicy"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ALWAYS"/&gt;
 *     &lt;enumeration value="ONLY"/&gt;
 *     &lt;enumeration value="PREFER"/&gt;
 *     &lt;enumeration value="NEVER"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "UrlReturnPolicy")
@XmlEnum
public enum UrlReturnPolicy {

    ALWAYS,
    ONLY,
    PREFER,
    NEVER;

    public String value() {
        return name();
    }

    public static UrlReturnPolicy fromValue(String v) {
        return valueOf(v);
    }

}

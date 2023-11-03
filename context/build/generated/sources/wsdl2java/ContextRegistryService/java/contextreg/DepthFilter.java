
package contextreg;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DepthFilter.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="DepthFilter"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="SINGLE"/&gt;
 *     &lt;enumeration value="SPECIFIED"/&gt;
 *     &lt;enumeration value="UNLIMITED"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "DepthFilter")
@XmlEnum
public enum DepthFilter {

    SINGLE,
    SPECIFIED,
    UNLIMITED;

    public String value() {
        return name();
    }

    public static DepthFilter fromValue(String v) {
        return valueOf(v);
    }

}

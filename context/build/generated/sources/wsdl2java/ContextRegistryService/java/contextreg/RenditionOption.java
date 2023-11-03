
package contextreg;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RenditionOption.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="RenditionOption"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CLIENT"/&gt;
 *     &lt;enumeration value="SERVER"/&gt;
 *     &lt;enumeration value="PRIMARY"/&gt;
 *     &lt;enumeration value="CLIENT_AND_SERVER"/&gt;
 *     &lt;enumeration value="PRIMARY_AND_CLIENT"/&gt;
 *     &lt;enumeration value="PRIMARY_AND_SERVER"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "RenditionOption")
@XmlEnum
public enum RenditionOption {

    CLIENT,
    SERVER,
    PRIMARY,
    CLIENT_AND_SERVER,
    PRIMARY_AND_CLIENT,
    PRIMARY_AND_SERVER;

    public String value() {
        return name();
    }

    public static RenditionOption fromValue(String v) {
        return valueOf(v);
    }

}

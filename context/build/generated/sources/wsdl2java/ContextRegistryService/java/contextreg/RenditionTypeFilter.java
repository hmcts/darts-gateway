
package contextreg;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RenditionTypeFilter.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="RenditionTypeFilter"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="SPECIFIED"/&gt;
 *     &lt;enumeration value="ANY"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "RenditionTypeFilter")
@XmlEnum
public enum RenditionTypeFilter {

    SPECIFIED,
    ANY;

    public String value() {
        return name();
    }

    public static RenditionTypeFilter fromValue(String v) {
        return valueOf(v);
    }

}

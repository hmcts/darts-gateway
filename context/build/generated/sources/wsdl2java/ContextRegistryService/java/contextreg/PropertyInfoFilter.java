
package contextreg;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PropertyInfoFilter.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="PropertyInfoFilter"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="NONE"/&gt;
 *     &lt;enumeration value="NON_INHERITED"/&gt;
 *     &lt;enumeration value="DISPLAY_CONFIG"/&gt;
 *     &lt;enumeration value="ALL"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "PropertyInfoFilter", namespace = "http://schema.core.datamodel.fs.documentum.emc.com/")
@XmlEnum
public enum PropertyInfoFilter {

    NONE,
    NON_INHERITED,
    DISPLAY_CONFIG,
    ALL;

    public String value() {
        return name();
    }

    public static PropertyInfoFilter fromValue(String v) {
        return valueOf(v);
    }

}

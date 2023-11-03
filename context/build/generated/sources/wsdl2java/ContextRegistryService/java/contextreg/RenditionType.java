
package contextreg;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RenditionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="RenditionType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CLIENT"/&gt;
 *     &lt;enumeration value="SERVER"/&gt;
 *     &lt;enumeration value="PRIMARY"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "RenditionType", namespace = "http://content.core.datamodel.fs.documentum.emc.com/")
@XmlEnum
public enum RenditionType {

    CLIENT,
    SERVER,
    PRIMARY;

    public String value() {
        return name();
    }

    public static RenditionType fromValue(String v) {
        return valueOf(v);
    }

}

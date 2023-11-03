
package contextreg;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VersionStrategy.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="VersionStrategy"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="IMPLIED"/&gt;
 *     &lt;enumeration value="NEXT_MAJOR"/&gt;
 *     &lt;enumeration value="NEXT_MINOR"/&gt;
 *     &lt;enumeration value="SAME_VERSION"/&gt;
 *     &lt;enumeration value="BRANCH_VERSION"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "VersionStrategy", namespace = "http://core.datamodel.fs.documentum.emc.com/")
@XmlEnum
public enum VersionStrategy {

    IMPLIED,
    NEXT_MAJOR,
    NEXT_MINOR,
    SAME_VERSION,
    BRANCH_VERSION;

    public String value() {
        return name();
    }

    public static VersionStrategy fromValue(String v) {
        return valueOf(v);
    }

}

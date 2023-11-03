
package contextreg;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PermissionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="PermissionType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="BASIC"/&gt;
 *     &lt;enumeration value="EXTENDED"/&gt;
 *     &lt;enumeration value="CUSTOM"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "PermissionType", namespace = "http://core.datamodel.fs.documentum.emc.com/")
@XmlEnum
public enum PermissionType {

    BASIC,
    EXTENDED,
    CUSTOM;

    public String value() {
        return name();
    }

    public static PermissionType fromValue(String v) {
        return valueOf(v);
    }

}

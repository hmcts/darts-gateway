
package contextreg;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ValueActionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="ValueActionType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="APPEND"/&gt;
 *     &lt;enumeration value="INSERT"/&gt;
 *     &lt;enumeration value="DELETE"/&gt;
 *     &lt;enumeration value="SET"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ValueActionType", namespace = "http://properties.core.datamodel.fs.documentum.emc.com/")
@XmlEnum
public enum ValueActionType {

    APPEND,
    INSERT,
    DELETE,
    SET;

    public String value() {
        return name();
    }

    public static ValueActionType fromValue(String v) {
        return valueOf(v);
    }

}

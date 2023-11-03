
package contextreg;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResultDataMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="ResultDataMode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="REFERENCE"/&gt;
 *     &lt;enumeration value="OBJECT"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ResultDataMode")
@XmlEnum
public enum ResultDataMode {

    REFERENCE,
    OBJECT;

    public String value() {
        return name();
    }

    public static ResultDataMode fromValue(String v) {
        return valueOf(v);
    }

}


package contextreg;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContentTransferMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="ContentTransferMode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="BASE64"/&gt;
 *     &lt;enumeration value="MTOM"/&gt;
 *     &lt;enumeration value="UCF"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ContentTransferMode", namespace = "http://content.core.datamodel.fs.documentum.emc.com/")
@XmlEnum
public enum ContentTransferMode {

    @XmlEnumValue("BASE64")
    BASE_64("BASE64"),
    MTOM("MTOM"),
    UCF("UCF");
    private final String value;

    ContentTransferMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ContentTransferMode fromValue(String v) {
        for (ContentTransferMode c: ContentTransferMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

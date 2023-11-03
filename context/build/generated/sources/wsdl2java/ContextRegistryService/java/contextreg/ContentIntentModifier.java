
package contextreg;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContentIntentModifier.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="ContentIntentModifier"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="SET"/&gt;
 *     &lt;enumeration value="INSERT"/&gt;
 *     &lt;enumeration value="REMOVE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ContentIntentModifier", namespace = "http://content.core.datamodel.fs.documentum.emc.com/")
@XmlEnum
public enum ContentIntentModifier {

    SET,
    INSERT,
    REMOVE;

    public String value() {
        return name();
    }

    public static ContentIntentModifier fromValue(String v) {
        return valueOf(v);
    }

}

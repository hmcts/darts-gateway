
package contextreg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LifecycleExecutionProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LifecycleExecutionProfile"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://profiles.core.datamodel.fs.documentum.emc.com/}Profile"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="testOnly" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="bypassEntryCriteria" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="resetToBase" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LifecycleExecutionProfile")
public class LifecycleExecutionProfile
    extends Profile
{

    @XmlAttribute(name = "testOnly", required = true)
    protected boolean testOnly;
    @XmlAttribute(name = "bypassEntryCriteria", required = true)
    protected boolean bypassEntryCriteria;
    @XmlAttribute(name = "resetToBase", required = true)
    protected boolean resetToBase;

    /**
     * Gets the value of the testOnly property.
     * 
     */
    public boolean isTestOnly() {
        return testOnly;
    }

    /**
     * Sets the value of the testOnly property.
     * 
     */
    public void setTestOnly(boolean value) {
        this.testOnly = value;
    }

    /**
     * Gets the value of the bypassEntryCriteria property.
     * 
     */
    public boolean isBypassEntryCriteria() {
        return bypassEntryCriteria;
    }

    /**
     * Sets the value of the bypassEntryCriteria property.
     * 
     */
    public void setBypassEntryCriteria(boolean value) {
        this.bypassEntryCriteria = value;
    }

    /**
     * Gets the value of the resetToBase property.
     * 
     */
    public boolean isResetToBase() {
        return resetToBase;
    }

    /**
     * Sets the value of the resetToBase property.
     * 
     */
    public void setResetToBase(boolean value) {
        this.resetToBase = value;
    }

}

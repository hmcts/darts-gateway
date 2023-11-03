
package contextreg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Profile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Profile"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Profile")
@XmlSeeAlso({
    ContentTransferProfile.class,
    PropertyProfile.class,
    ContentProfile.class,
    ClusteringProfile.class,
    RelationshipProfile.class,
    PermissionProfile.class,
    SchemaProfile.class,
    DeleteProfile.class,
    CheckinProfile.class,
    CheckoutProfile.class,
    CopyProfile.class,
    MoveProfile.class,
    RichTextProfile.class,
    SearchProfile.class,
    LifecycleExecutionProfile.class,
    VdmUpdateProfile.class,
    VdmRetrieveProfile.class,
    CreateProfile.class
})
public abstract class Profile {


}

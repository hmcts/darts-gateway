
package contextreg;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the contextreg package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Lookup_QNAME = new QName("http://services.rt.fs.documentum.emc.com/", "lookup");
    private final static QName _LookupResponse_QNAME = new QName("http://services.rt.fs.documentum.emc.com/", "lookupResponse");
    private final static QName _Register_QNAME = new QName("http://services.rt.fs.documentum.emc.com/", "register");
    private final static QName _RegisterResponse_QNAME = new QName("http://services.rt.fs.documentum.emc.com/", "registerResponse");
    private final static QName _Unregister_QNAME = new QName("http://services.rt.fs.documentum.emc.com/", "unregister");
    private final static QName _UnregisterResponse_QNAME = new QName("http://services.rt.fs.documentum.emc.com/", "unregisterResponse");
    private final static QName _ServiceContext_QNAME = new QName("http://context.core.datamodel.fs.documentum.emc.com/", "ServiceContext");
    private final static QName _ContentRenditionType_QNAME = new QName("http://content.core.datamodel.fs.documentum.emc.com/", "renditionType");
    private final static QName _ContentIntentModifier_QNAME = new QName("http://content.core.datamodel.fs.documentum.emc.com/", "intentModifier");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: contextreg
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Lookup }
     * 
     */
    public Lookup createLookup() {
        return new Lookup();
    }

    /**
     * Create an instance of {@link LookupResponse }
     * 
     */
    public LookupResponse createLookupResponse() {
        return new LookupResponse();
    }

    /**
     * Create an instance of {@link Register }
     * 
     */
    public Register createRegister() {
        return new Register();
    }

    /**
     * Create an instance of {@link RegisterResponse }
     * 
     */
    public RegisterResponse createRegisterResponse() {
        return new RegisterResponse();
    }

    /**
     * Create an instance of {@link Unregister }
     * 
     */
    public Unregister createUnregister() {
        return new Unregister();
    }

    /**
     * Create an instance of {@link UnregisterResponse }
     * 
     */
    public UnregisterResponse createUnregisterResponse() {
        return new UnregisterResponse();
    }

    /**
     * Create an instance of {@link ClusteringStrategy }
     * 
     */
    public ClusteringStrategy createClusteringStrategy() {
        return new ClusteringStrategy();
    }

    /**
     * Create an instance of {@link PropertySet }
     * 
     */
    public PropertySet createPropertySet() {
        return new PropertySet();
    }

    /**
     * Create an instance of {@link StringProperty }
     * 
     */
    public StringProperty createStringProperty() {
        return new StringProperty();
    }

    /**
     * Create an instance of {@link NumberProperty }
     * 
     */
    public NumberProperty createNumberProperty() {
        return new NumberProperty();
    }

    /**
     * Create an instance of {@link DateProperty }
     * 
     */
    public DateProperty createDateProperty() {
        return new DateProperty();
    }

    /**
     * Create an instance of {@link BooleanProperty }
     * 
     */
    public BooleanProperty createBooleanProperty() {
        return new BooleanProperty();
    }

    /**
     * Create an instance of {@link ObjectIdProperty }
     * 
     */
    public ObjectIdProperty createObjectIdProperty() {
        return new ObjectIdProperty();
    }

    /**
     * Create an instance of {@link StringArrayProperty }
     * 
     */
    public StringArrayProperty createStringArrayProperty() {
        return new StringArrayProperty();
    }

    /**
     * Create an instance of {@link ValueAction }
     * 
     */
    public ValueAction createValueAction() {
        return new ValueAction();
    }

    /**
     * Create an instance of {@link NumberArrayProperty }
     * 
     */
    public NumberArrayProperty createNumberArrayProperty() {
        return new NumberArrayProperty();
    }

    /**
     * Create an instance of {@link BooleanArrayProperty }
     * 
     */
    public BooleanArrayProperty createBooleanArrayProperty() {
        return new BooleanArrayProperty();
    }

    /**
     * Create an instance of {@link DateArrayProperty }
     * 
     */
    public DateArrayProperty createDateArrayProperty() {
        return new DateArrayProperty();
    }

    /**
     * Create an instance of {@link ObjectIdArrayProperty }
     * 
     */
    public ObjectIdArrayProperty createObjectIdArrayProperty() {
        return new ObjectIdArrayProperty();
    }

    /**
     * Create an instance of {@link RichTextProperty }
     * 
     */
    public RichTextProperty createRichTextProperty() {
        return new RichTextProperty();
    }

    /**
     * Create an instance of {@link ContentTransferProfile }
     * 
     */
    public ContentTransferProfile createContentTransferProfile() {
        return new ContentTransferProfile();
    }

    /**
     * Create an instance of {@link PropertyProfile }
     * 
     */
    public PropertyProfile createPropertyProfile() {
        return new PropertyProfile();
    }

    /**
     * Create an instance of {@link ContentProfile }
     * 
     */
    public ContentProfile createContentProfile() {
        return new ContentProfile();
    }

    /**
     * Create an instance of {@link ClusteringProfile }
     * 
     */
    public ClusteringProfile createClusteringProfile() {
        return new ClusteringProfile();
    }

    /**
     * Create an instance of {@link RelationshipProfile }
     * 
     */
    public RelationshipProfile createRelationshipProfile() {
        return new RelationshipProfile();
    }

    /**
     * Create an instance of {@link PermissionProfile }
     * 
     */
    public PermissionProfile createPermissionProfile() {
        return new PermissionProfile();
    }

    /**
     * Create an instance of {@link SchemaProfile }
     * 
     */
    public SchemaProfile createSchemaProfile() {
        return new SchemaProfile();
    }

    /**
     * Create an instance of {@link DeleteProfile }
     * 
     */
    public DeleteProfile createDeleteProfile() {
        return new DeleteProfile();
    }

    /**
     * Create an instance of {@link CheckinProfile }
     * 
     */
    public CheckinProfile createCheckinProfile() {
        return new CheckinProfile();
    }

    /**
     * Create an instance of {@link CheckoutProfile }
     * 
     */
    public CheckoutProfile createCheckoutProfile() {
        return new CheckoutProfile();
    }

    /**
     * Create an instance of {@link CopyProfile }
     * 
     */
    public CopyProfile createCopyProfile() {
        return new CopyProfile();
    }

    /**
     * Create an instance of {@link MoveProfile }
     * 
     */
    public MoveProfile createMoveProfile() {
        return new MoveProfile();
    }

    /**
     * Create an instance of {@link RichTextProfile }
     * 
     */
    public RichTextProfile createRichTextProfile() {
        return new RichTextProfile();
    }

    /**
     * Create an instance of {@link SearchProfile }
     * 
     */
    public SearchProfile createSearchProfile() {
        return new SearchProfile();
    }

    /**
     * Create an instance of {@link LifecycleExecutionProfile }
     * 
     */
    public LifecycleExecutionProfile createLifecycleExecutionProfile() {
        return new LifecycleExecutionProfile();
    }

    /**
     * Create an instance of {@link VdmUpdateProfile }
     * 
     */
    public VdmUpdateProfile createVdmUpdateProfile() {
        return new VdmUpdateProfile();
    }

    /**
     * Create an instance of {@link VdmRetrieveProfile }
     * 
     */
    public VdmRetrieveProfile createVdmRetrieveProfile() {
        return new VdmRetrieveProfile();
    }

    /**
     * Create an instance of {@link CreateProfile }
     * 
     */
    public CreateProfile createCreateProfile() {
        return new CreateProfile();
    }

    /**
     * Create an instance of {@link ObjectId }
     * 
     */
    public ObjectId createObjectId() {
        return new ObjectId();
    }

    /**
     * Create an instance of {@link RichText }
     * 
     */
    public RichText createRichText() {
        return new RichText();
    }

    /**
     * Create an instance of {@link ServiceContext }
     * 
     */
    public ServiceContext createServiceContext() {
        return new ServiceContext();
    }

    /**
     * Create an instance of {@link BasicIdentity }
     * 
     */
    public BasicIdentity createBasicIdentity() {
        return new BasicIdentity();
    }

    /**
     * Create an instance of {@link SsoIdentity }
     * 
     */
    public SsoIdentity createSsoIdentity() {
        return new SsoIdentity();
    }

    /**
     * Create an instance of {@link RepositoryIdentity }
     * 
     */
    public RepositoryIdentity createRepositoryIdentity() {
        return new RepositoryIdentity();
    }

    /**
     * Create an instance of {@link OverridePermission }
     * 
     */
    public OverridePermission createOverridePermission() {
        return new OverridePermission();
    }

    /**
     * Create an instance of {@link ActivityInfo }
     * 
     */
    public ActivityInfo createActivityInfo() {
        return new ActivityInfo();
    }

    /**
     * Create an instance of {@link BinaryContent }
     * 
     */
    public BinaryContent createBinaryContent() {
        return new BinaryContent();
    }

    /**
     * Create an instance of {@link DataHandlerContent }
     * 
     */
    public DataHandlerContent createDataHandlerContent() {
        return new DataHandlerContent();
    }

    /**
     * Create an instance of {@link UcfContent }
     * 
     */
    public UcfContent createUcfContent() {
        return new UcfContent();
    }

    /**
     * Create an instance of {@link UrlContent }
     * 
     */
    public UrlContent createUrlContent() {
        return new UrlContent();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Lookup }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Lookup }{@code >}
     */
    @XmlElementDecl(namespace = "http://services.rt.fs.documentum.emc.com/", name = "lookup")
    public JAXBElement<Lookup> createLookup(Lookup value) {
        return new JAXBElement<Lookup>(_Lookup_QNAME, Lookup.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LookupResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link LookupResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://services.rt.fs.documentum.emc.com/", name = "lookupResponse")
    public JAXBElement<LookupResponse> createLookupResponse(LookupResponse value) {
        return new JAXBElement<LookupResponse>(_LookupResponse_QNAME, LookupResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Register }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Register }{@code >}
     */
    @XmlElementDecl(namespace = "http://services.rt.fs.documentum.emc.com/", name = "register")
    public JAXBElement<Register> createRegister(Register value) {
        return new JAXBElement<Register>(_Register_QNAME, Register.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RegisterResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://services.rt.fs.documentum.emc.com/", name = "registerResponse")
    public JAXBElement<RegisterResponse> createRegisterResponse(RegisterResponse value) {
        return new JAXBElement<RegisterResponse>(_RegisterResponse_QNAME, RegisterResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Unregister }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Unregister }{@code >}
     */
    @XmlElementDecl(namespace = "http://services.rt.fs.documentum.emc.com/", name = "unregister")
    public JAXBElement<Unregister> createUnregister(Unregister value) {
        return new JAXBElement<Unregister>(_Unregister_QNAME, Unregister.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnregisterResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link UnregisterResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://services.rt.fs.documentum.emc.com/", name = "unregisterResponse")
    public JAXBElement<UnregisterResponse> createUnregisterResponse(UnregisterResponse value) {
        return new JAXBElement<UnregisterResponse>(_UnregisterResponse_QNAME, UnregisterResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceContext }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ServiceContext }{@code >}
     */
    @XmlElementDecl(namespace = "http://context.core.datamodel.fs.documentum.emc.com/", name = "ServiceContext")
    public JAXBElement<ServiceContext> createServiceContext(ServiceContext value) {
        return new JAXBElement<ServiceContext>(_ServiceContext_QNAME, ServiceContext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RenditionType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RenditionType }{@code >}
     */
    @XmlElementDecl(namespace = "http://content.core.datamodel.fs.documentum.emc.com/", name = "renditionType", scope = Content.class)
    public JAXBElement<RenditionType> createContentRenditionType(RenditionType value) {
        return new JAXBElement<RenditionType>(_ContentRenditionType_QNAME, RenditionType.class, Content.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContentIntentModifier }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ContentIntentModifier }{@code >}
     */
    @XmlElementDecl(namespace = "http://content.core.datamodel.fs.documentum.emc.com/", name = "intentModifier", scope = Content.class)
    public JAXBElement<ContentIntentModifier> createContentIntentModifier(ContentIntentModifier value) {
        return new JAXBElement<ContentIntentModifier>(_ContentIntentModifier_QNAME, ContentIntentModifier.class, Content.class, value);
    }

}

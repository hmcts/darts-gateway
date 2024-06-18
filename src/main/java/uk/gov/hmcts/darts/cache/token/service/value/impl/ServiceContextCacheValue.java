package uk.gov.hmcts.darts.cache.token.service.value.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import documentum.contextreg.BasicIdentity;
import documentum.contextreg.Identity;
import documentum.contextreg.ObjectFactory;
import documentum.contextreg.ServiceContext;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import  jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.xml.transform.StringSource;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;
import uk.gov.hmcts.darts.cache.token.service.TokenRegisterable;
import uk.gov.hmcts.darts.cache.token.service.value.CacheValue;

import java.io.StringWriter;

@JsonTypeName("ServiceContextCacheValue")
public class ServiceContextCacheValue implements CacheValue {
    private String contextStr;

    private String sharedKey;

    private ServiceContext context;

    protected JAXBContext jaxbContext;

    @SuppressWarnings("PMD.CallSuperInConstructor")
    public ServiceContextCacheValue() {
        //Empty constructor
    }

    public ServiceContextCacheValue(ServiceContext context) {

        try {
            jaxbContext = JAXBContext.newInstance(ServiceContext.class);
            JAXBElement<ServiceContext> servicecontext =  new ObjectFactory().createServiceContext(context);
            StringWriter sw = new StringWriter();
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.marshal(servicecontext, sw);
            contextStr = sw.toString();
        } catch (JAXBException e) {
            throw new CacheException(e);
        }
    }

    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public ServiceContextCacheValue(ServiceContextCacheValue value) {
        try {
            jaxbContext = JAXBContext.newInstance(ServiceContext.class);
            setSharedKey(value.getSharedKey());
            setContextString(value.getContextString());
        } catch (JAXBException e) {
            throw new CacheException(e);
        }
    }


    @Override
    public String getContextString() {
        return contextStr;
    }

    public void setContextString(String context) {
        this.contextStr = context;
    }

    @JsonIgnore
    @Override
    public ServiceContext getServiceContext() {

        if (context == null) {
            try {
                Unmarshaller jaxbMarshaller = jaxbContext.createUnmarshaller();
                StringSource ss = new StringSource(getContextString());
                context = jaxbMarshaller.unmarshal(ss, ServiceContext.class).getValue();
            } catch (JAXBException e) {
                throw new CacheException(e);
            }
        }

        return context;
    }

    private static String getUserName(ServiceContext serviceContext) {
        Identity identity = serviceContext.getIdentities().get(0);
        if (identity instanceof BasicIdentity basicIdentity) {
            return basicIdentity.getUserName();
        }

        throw new CacheException("Do not understand the service context");
    }

    private static String getPassword(ServiceContext serviceContext) {
        Identity identity = serviceContext.getIdentities().get(0);
        if (identity instanceof BasicIdentity basicIdentity) {
            return basicIdentity.getPassword();
        }

        throw new CacheException("Do not understand the service context");
    }

    @Override
    public String getSharedKey() {
        if (sharedKey == null) {
            sharedKey = getId(getServiceContext());
        }
        return sharedKey;
    }


    public static String getId(ServiceContext context) {
        return TokenRegisterable.CACHE_PREFIX + ":" + getUserName(context) + ":" + getPassword(context);
    }

    public void setSharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
    }
}

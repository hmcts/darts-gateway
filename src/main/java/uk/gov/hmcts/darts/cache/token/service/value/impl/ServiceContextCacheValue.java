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

    private String id;

    private ServiceContext context;

    protected static final String EMPTY_DOWN_STREAM_TOKEN = "";

    private JAXBContext jaxbContext;

    public ServiceContextCacheValue() throws CacheException {
    }

    public ServiceContextCacheValue(ServiceContext context) throws CacheException {

        try {
            JAXBElement<ServiceContext> scontext =  new ObjectFactory().createServiceContext(context);
            StringWriter sw = new StringWriter();
            JAXBContext jaxbContext = JAXBContext.newInstance(ServiceContext.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.marshal(scontext, sw);
            contextStr = sw.toString();
        } catch (JAXBException e) {
            throw new CacheException(e);
        }
    }

    public ServiceContextCacheValue(ServiceContextCacheValue value) {
        try {
            jaxbContext = JAXBContext.newInstance(ServiceContext.class);
        } catch (JAXBException e) {
            throw new CacheException(e);
        }
        setId(value.getId());
        setContextString(value.getContextString());

    }


    public String getContextString() {
        return contextStr;
    }

    public void setContextString(String context) {
        this.contextStr = context;
    }

    @JsonIgnore
    public ServiceContext getServiceContext() throws CacheException {

        if (context == null) {
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(ServiceContext.class);
                Unmarshaller jaxbMarshaller = jaxbContext.createUnmarshaller();
                StringSource ss = new StringSource(getContextString());
                context = jaxbMarshaller.unmarshal(ss, ServiceContext.class).getValue();
            } catch (JAXBException e) {
                throw new CacheException(e);
            }
        }

        return context;
    }

    private String getUserName() throws CacheException {
        ServiceContext context = getServiceContext();
        Identity identity = context.getIdentities().get(0);
        if (identity instanceof BasicIdentity basicIdentity) {
            return basicIdentity.getUserName();
        }

        throw new CacheException("Do not understand the service context");
    }

    private String getPassword() throws CacheException {
        ServiceContext context = getServiceContext();
        Identity identity = context.getIdentities().get(0);
        if (identity instanceof BasicIdentity basicIdentity) {
            return basicIdentity.getPassword();
        }

        throw new CacheException("Do not understand the service context");
    }

    @Override
    public String getId() throws CacheException {
        if (id == null) {
            id = TokenRegisterable.CACHE_PREFIX + ":" + getUserName() + ":" + getPassword();
        }
        return id;
    }

    public void setId(String id) throws CacheException {
        this.id = id;
    }
}

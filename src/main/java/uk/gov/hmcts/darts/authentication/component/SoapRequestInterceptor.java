package uk.gov.hmcts.darts.authentication.component;

import documentum.contextreg.BasicIdentity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapHeaderException;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.soap.server.SoapEndpointInterceptor;
import uk.gov.hmcts.darts.cache.token.TokenRegisterable;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.xml.namespace.QName;

@Component
@RequiredArgsConstructor
@Slf4j
public class SoapRequestInterceptor implements SoapEndpointInterceptor {

    private static final String SERVICE_CONTEXT_HEADER = "{http://context.core.datamodel.fs.documentum.emc.com/}ServiceContext";
    private final SoapHeaderConverter soapHeaderConverter;
    private final TokenRegisterable tokenRegisterable;

    @Override
    public boolean understands(SoapHeaderElement header) {
        return true;
    }

    @Override
    public boolean handleRequest(MessageContext messageContext, Object endpoint) {
        SaajSoapMessage message = (SaajSoapMessage) messageContext.getRequest();
        handleServiceContextSoapHeader(message.getSoapHeader());
        return true; // continue processing of the request interceptor chain
    }

    private boolean handleServiceContextSoapHeader(SoapHeader soapHeader) {
        AtomicBoolean isAccessTokenRequestAttrSet = new AtomicBoolean(false);
        try {
            Iterator<SoapHeaderElement> serviceContextSoapHeaderElementIt = soapHeader.examineHeaderElements(
                QName.valueOf(SERVICE_CONTEXT_HEADER));
            while (serviceContextSoapHeaderElementIt.hasNext()) {
                SoapHeaderElement soapHeaderElement = serviceContextSoapHeaderElementIt.next();
                soapHeaderConverter.convertSoapHeader(soapHeaderElement).ifPresent(serviceContext -> {
                    Optional<BasicIdentity> basicIdentityOptional = serviceContext.getIdentities()
                        .stream()
                        .filter(identity -> identity instanceof BasicIdentity)
                        .map(identity -> (BasicIdentity) identity)
                        .filter(basicIdentity -> StringUtils.isNotBlank(basicIdentity.getUserName()))
                        .filter(basicIdentity -> StringUtils.isNotBlank(basicIdentity.getPassword()))
                        .findFirst();
                    if (basicIdentityOptional.isPresent()) {
                        tokenRegisterable.createToken(serviceContext)
                            .ifPresent(cacheToken ->
                                           new SecurityRequestAttributesWrapper(RequestContextHolder.currentRequestAttributes()).setAuthenticationToken(
                                   cacheToken.getToken()
                            ));
                        isAccessTokenRequestAttrSet.set(true);
                    }
                });

                if (isAccessTokenRequestAttrSet.get()) {
                    break;
                }
            }
        } catch (SoapHeaderException soapHeaderException) {
            log.info("The ServiceContext header cannot be returned", soapHeaderException);
        }

        return isAccessTokenRequestAttrSet.get();
    }

    @Override
    public boolean handleResponse(MessageContext messageContext, Object endpoint) {
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext, Object endpoint) {
        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Object endpoint, Exception ex) {

    }

}

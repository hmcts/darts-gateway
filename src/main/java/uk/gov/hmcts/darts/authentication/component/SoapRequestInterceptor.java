package uk.gov.hmcts.darts.authentication.component;

import com.google.common.collect.Iterators;
import documentum.contextreg.BasicIdentity;
import documentum.contextreg.Identity;
import documentum.contextreg.ServiceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.soap.server.SoapEndpointInterceptor;
import org.w3c.dom.Node;
import uk.gov.hmcts.darts.authentication.exception.AuthenticationFailedException;
import uk.gov.hmcts.darts.authentication.exception.DocumentumUnknownTokenSoapException;
import uk.gov.hmcts.darts.authentication.exception.InvalidIdentitiesFoundException;
import uk.gov.hmcts.darts.authentication.exception.NoIdentitiesFoundException;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;
import uk.gov.hmcts.darts.cache.token.exception.CacheTokenCreationException;
import uk.gov.hmcts.darts.cache.token.service.Token;
import uk.gov.hmcts.darts.cache.token.service.TokenRegisterable;
import uk.gov.hmcts.darts.cache.token.service.value.CacheValue;
import uk.gov.hmcts.darts.cache.token.service.value.DownstreamTokenisableValue;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;

@Component
@RequiredArgsConstructor
@Slf4j
public class SoapRequestInterceptor implements SoapEndpointInterceptor {

    private static final String SERVICE_CONTEXT_HEADER = "{http://context.core.datamodel.fs.documentum.emc.com/}ServiceContext";
    private static final String SECURITY_HEADER = "{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Security";

    private final SoapHeaderConverter soapHeaderConverter;
    private final SoapBodyConverter soapBodyConverter;
    private final TokenRegisterable tokenRegisterable;
    private final SecurityProperties securityProperties;

    private static final String NEW_LINE = System.getProperty("line.separator");

    private static final String MESSAGE_SEPERATOR = "----------------------------";

    @Override
    public boolean understands(SoapHeaderElement header) {
        return true;
    }

    @Override
    public boolean handleRequest(MessageContext messageContext, Object endpoint) {
        logPayloadMessage("REQUEST PAYLOAD IS {}", messageContext.getRequest());

        SaajSoapMessage message = (SaajSoapMessage) messageContext.getRequest();
        handleRequest(message);
        return true; // continue processing of the request interceptor chain
    }

    private void handleRequest(SaajSoapMessage soapMessage) {
        if (isTokenAuthentication(soapMessage)) {
            authenticateToken(soapMessage);
        } else {
            authenticateUsernameAndPassword(soapMessage);
        }
    }

    private boolean isTokenAuthentication(SaajSoapMessage message) {
        var soapHeader = message.getSoapHeader();
        if (soapHeader == null) {
            return false;
        }
        Iterator<SoapHeaderElement> serviceContextSoapHeaderElementIt = soapHeader.examineHeaderElements(
            QName.valueOf(SECURITY_HEADER));
        return serviceContextSoapHeaderElementIt.hasNext();
    }

    private boolean authenticateToken(SaajSoapMessage message) {
        var soapHeader = message.getSoapHeader();
        Iterator<SoapHeaderElement> securityToken = soapHeader.examineHeaderElements(
            QName.valueOf(SECURITY_HEADER));

        Optional<String> tokenToReturn = Optional.empty();
        if (securityToken.hasNext()) {
            SoapHeaderElement securityTokenElement = securityToken.next();
            tokenToReturn = soapHeaderConverter.convertSoapHeaderToToken(securityTokenElement);
            String specifiedtoken = tokenToReturn.orElse("N/K");
            Token foundTokenInCache = tokenRegisterable.getToken(specifiedtoken);
            Optional<CacheValue> optRefreshableCacheValue = tokenRegisterable.lookup(foundTokenInCache);

            if (optRefreshableCacheValue.isEmpty()) {
                throw new DocumentumUnknownTokenSoapException(foundTokenInCache.getTokenString());
            } else {
                if (optRefreshableCacheValue.get() instanceof DownstreamTokenisableValue downstreamTokenisable) {
                    new SecurityRequestAttributesWrapper(RequestContextHolder.currentRequestAttributes()).setAuthenticationToken(
                        downstreamTokenisable);
                } else {
                    new SecurityRequestAttributesWrapper(RequestContextHolder.currentRequestAttributes()).setAuthenticationToken(
                        specifiedtoken);
                }
            }
        }

        if (tokenToReturn.isEmpty()) {
            throw new DocumentumUnknownTokenSoapException("");
        }

        return true;
    }

    private void authenticateUsernameAndPassword(SaajSoapMessage message) throws AuthenticationFailedException {
        Node bodyNode = ((DOMSource) message.getSoapBody().getPayloadSource()).getNode();
        String messageEndpoint = bodyNode.getLocalName();
        if (ContextRegistryPayload.isApplicable(message, ContextRegistryPayload.ContextRegistryOperation.REGISTRY_OPERATION)) {
            authenticateUsernameAndPasswordFromBody(message);
        } else {
            authenticateUsernameAndPasswordFromHeader(message);
        }
    }

    private void authenticateUsernameAndPasswordFromBody(SaajSoapMessage message) throws AuthenticationFailedException {
        Optional<ServiceContext> serviceContextOpt = soapBodyConverter.getServiceContext(message);
        if (serviceContextOpt.isEmpty()) {
            throw new NoIdentitiesFoundException();
        }
        try {
            getAuthenticationToken(message, serviceContextOpt.get());
        } catch (CacheTokenCreationException tokenCreationException) {
            throw new AuthenticationFailedException(tokenCreationException);
        }
    }


    private boolean authenticateUsernameAndPasswordFromHeader(SaajSoapMessage message) throws AuthenticationFailedException {
        var soapHeader = message.getSoapHeader();
        Iterator<SoapHeaderElement> serviceContextSoapHeaderElementIt = soapHeader.examineHeaderElements(
            QName.valueOf(SERVICE_CONTEXT_HEADER));
        try {
            int size = Iterators.size(serviceContextSoapHeaderElementIt);
            if (size == 1) {
                serviceContextSoapHeaderElementIt = soapHeader.examineHeaderElements(
                    QName.valueOf(SERVICE_CONTEXT_HEADER));
                while (serviceContextSoapHeaderElementIt.hasNext()) {
                    SoapHeaderElement soapHeaderElement = serviceContextSoapHeaderElementIt.next();
                    Optional<ServiceContext> serviceContextOpt = soapHeaderConverter.convertSoapHeader(soapHeaderElement);
                    if (serviceContextOpt.isPresent()) {
                        ServiceContext serviceContext = serviceContextOpt.get();
                        if (!identitiesPresent(soapHeaderElement)) {
                            throw new NoIdentitiesFoundException();
                        }

                        getAuthenticationToken(message, serviceContext);
                    }
                }
            } else {
                throw new NoIdentitiesFoundException();
            }
        } catch (CacheTokenCreationException tokenCreationException) {
            throw new AuthenticationFailedException(tokenCreationException);
        }
        return true;
    }


    private void getAuthenticationToken(SaajSoapMessage message, ServiceContext serviceContext)
        throws AuthenticationFailedException, InvalidIdentitiesFoundException {
        List<Identity> identities = serviceContext.getIdentities();
        if (CollectionUtils.isEmpty(identities)) {
            throw new NoIdentitiesFoundException();
        }
        Optional<BasicIdentity> basicIdentityOptional = identities
            .stream()
            .filter(identity -> identity instanceof BasicIdentity)
            .map(identity -> (BasicIdentity) identity)
            .filter(basicIdentity -> StringUtils.isNotBlank(basicIdentity.getUserName()))
            .filter(basicIdentity -> StringUtils.isNotBlank(basicIdentity.getPassword()))
            .findAny();
        if (basicIdentityOptional.isEmpty()) {
            throw new InvalidIdentitiesFoundException();
        }

        verifyBasicAuthorisationRequestIsAllowed(basicIdentityOptional.get().getUserName(), message);

        // always reuse the tokenOpt in the case of authentication
        Optional<Token> tokenOpt = tokenRegisterable.store(serviceContext, true);

        if (tokenOpt.isEmpty()) {
            throw new AuthenticationFailedException();
        }
        Token token = tokenOpt.get();
        Optional<CacheValue> optRefreshableCacheValue = tokenRegisterable.lookup(token);
        CacheValue refreshableCacheValue = optRefreshableCacheValue.orElse(null);

        if (refreshableCacheValue instanceof DownstreamTokenisableValue downstreamTokenisable) {
            Token tokenDownstream = downstreamTokenisable.getToken();
                new SecurityRequestAttributesWrapper(RequestContextHolder.currentRequestAttributes()).setAuthenticationToken(
                    tokenDownstream.getTokenString());
        } else if (token.getTokenString().isEmpty()) {
            throw new AuthenticationFailedException();
        } else {
            new SecurityRequestAttributesWrapper(RequestContextHolder.currentRequestAttributes()).setAuthenticationToken(
                token.getTokenString());
        }
    }

    private boolean identitiesPresent(SoapHeaderElement soapHeader) {
        Optional<ServiceContext> context = soapHeaderConverter.convertSoapHeader(soapHeader);

        return context.filter(serviceContext -> Iterators.size(serviceContext.getIdentities().iterator()) > 0).isPresent();
    }

    @Override
    public boolean handleResponse(MessageContext messageContext, Object endpoint) {
        logPayloadMessage("RESPONSE PAYLOAD IS {}", messageContext.getResponse());
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext, Object endpoint) {
        logPayloadMessage("FAULT PAYLOAD IS {}", messageContext.getResponse());
        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Object endpoint, Exception ex) {

    }

    private void logPayloadMessage(String messagePrefix, WebServiceMessage message) {
        try {
            ByteArrayTransportOutputStream byteArrayTransportOutputStream =
                new ByteArrayTransportOutputStream();
            message.writeTo(byteArrayTransportOutputStream);

            String payloadMessage = NEW_LINE + MESSAGE_SEPERATOR
                                    + NEW_LINE + new String(byteArrayTransportOutputStream.toByteArray()) + NEW_LINE
                                    + MESSAGE_SEPERATOR + NEW_LINE;

            log.trace(messagePrefix, payloadMessage);
        } catch (IOException ex) {
            log.error("Failed to write SOAP message", ex);
        }
    }

    private void verifyBasicAuthorisationRequestIsAllowed(String userName, SaajSoapMessage message) {
        if (isContextRegistryRequest(message)) {
            return;
        }

        if (!securityProperties.isUserWhitelisted(userName)) {
            throw new AuthenticationFailedException();
        }
    }

    private boolean isContextRegistryRequest(SaajSoapMessage message) {
        if (ContextRegistryPayload.isApplicable(message, ContextRegistryPayload.ContextRegistryOperation.REGISTRY_OPERATION)
            || ContextRegistryPayload.isApplicable(message, ContextRegistryPayload.ContextRegistryOperation.LOOKUP_OPERATION)
            || ContextRegistryPayload.isApplicable(message, ContextRegistryPayload.ContextRegistryOperation.UNREGISTER_OPERATION)) {
            return true;
        }

        return false;
    }
}

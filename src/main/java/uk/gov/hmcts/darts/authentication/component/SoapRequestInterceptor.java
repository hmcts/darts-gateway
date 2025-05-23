package uk.gov.hmcts.darts.authentication.component;

import com.emc.documentum.fs.rt.ServiceContextLookupException;
import com.google.common.collect.Iterators;
import documentum.contextreg.BasicIdentity;
import documentum.contextreg.Identity;
import documentum.contextreg.ServiceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.soap.server.SoapEndpointInterceptor;
import uk.gov.hmcts.darts.authentication.exception.AuthenticationFailedException;
import uk.gov.hmcts.darts.authentication.exception.InvalidIdentitiesFoundException;
import uk.gov.hmcts.darts.authentication.exception.NoIdentitiesFoundException;
import uk.gov.hmcts.darts.cache.AuthSupport;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;
import uk.gov.hmcts.darts.log.conf.ExcludePayloadLogging;
import uk.gov.hmcts.darts.log.conf.LogProperties;

import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;

@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("PMD.GodClass")
public class SoapRequestInterceptor implements SoapEndpointInterceptor {

    private static final String SERVICE_CONTEXT_HEADER = "{http://context.core.datamodel.fs.documentum.emc.com/}ServiceContext";
    private static final String SECURITY_HEADER = "{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Security";
    private static final String JSESSIONID_KEY = "JSESSIONID";

    private final AuthSupport authSupport;

    private final SoapHeaderConverter soapHeaderConverter;
    private final SoapBodyConverter soapBodyConverter;
    private final SecurityProperties securityProperties;

    private static final String NEW_LINE = System.getProperty("line.separator");

    private static final String MESSAGE_SEPERATOR = "----------------------------";

    private final LogProperties logProperties;

    public static final String REQUEST_PAYLOAD_PREFIX = "REQUEST PAYLOAD {}";

    public static final String RESPONSE_PAYLOAD_PREFIX = "RESPONSE PAYLOAD {}";

    public static final String FAULT_PAYLOAD_IS = "FAULT PAYLOAD IS {}";

    @Override
    public boolean understands(SoapHeaderElement header) {
        return true;
    }

    @Override
    public boolean handleRequest(MessageContext messageContext, Object endpoint) {
        logPayloadMessage(REQUEST_PAYLOAD_PREFIX, messageContext.getRequest());

        SaajSoapMessage message = (SaajSoapMessage) messageContext.getRequest();
        if (isTokenAuthentication(message)) {
            authenticateUsingToken(message);
        } else {
            authenticateUsingUsernameAndPassword(message);
        }
        return true; // continue processing of the request interceptor chain
    }

    private boolean isTokenAuthentication(SaajSoapMessage message) {
        SoapHeader soapHeader = message.getSoapHeader();
        if (soapHeader == null) {
            return false;
        }
        Iterator<SoapHeaderElement> serviceContextSoapHeaderElementIt = soapHeader.examineHeaderElements(
            QName.valueOf(SECURITY_HEADER));
        return serviceContextSoapHeaderElementIt.hasNext();
    }


    private void setupToken(String token) {
        new SecurityRequestAttributesWrapper(RequestContextHolder.currentRequestAttributes()).setAuthenticationToken(token);
    }

    private void authenticateUsingToken(SaajSoapMessage message) {
        SoapHeader soapHeader = message.getSoapHeader();
        Iterator<SoapHeaderElement> securityToken = soapHeader.examineHeaderElements(
            QName.valueOf(SECURITY_HEADER));

        Optional<String> tokenToReturn = Optional.empty();
        if (securityToken.hasNext()) {
            SoapHeaderElement securityTokenElement = securityToken.next();
            tokenToReturn = soapHeaderConverter.convertSoapHeaderToToken(securityTokenElement);
            String token = tokenToReturn.orElse("N/K");
            authSupport.validateToken(token);
            setupToken(token);
        }
        if (tokenToReturn.isEmpty()) {
            throw new ServiceContextLookupException("");
        }
    }

    private void authenticateUsingUsernameAndPassword(SaajSoapMessage message) {
        if (ContextRegistryPayload.isApplicable(message, ContextRegistryPayload.ContextRegistryOperation.REGISTRY_OPERATION)) {
            authenticateUsernameAndPasswordFromBody(message);
        } else {
            authenticateUsernameAndPasswordFromHeader(message);
        }
    }

    private void authenticateUsernameAndPasswordFromBody(SaajSoapMessage message) {
        Optional<ServiceContext> serviceContextOpt = soapBodyConverter.getServiceContext(message);
        if (serviceContextOpt.isEmpty()) {
            throw new NoIdentitiesFoundException();
        }
        try {
            getAuthenticationToken(message, serviceContextOpt.get());
        } catch (Exception exception) {
            throw new AuthenticationFailedException(exception);
        }
    }


    private boolean authenticateUsernameAndPasswordFromHeader(SaajSoapMessage message) {
        SoapHeader soapHeader = message.getSoapHeader();
        Iterator<SoapHeaderElement> serviceContextSoapHeaderElementIt = soapHeader.examineHeaderElements(
            QName.valueOf(SERVICE_CONTEXT_HEADER));
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

        return true;
    }


    @SuppressWarnings("PMD.CyclomaticComplexity")
    private void getAuthenticationToken(SaajSoapMessage message, ServiceContext serviceContext) {
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

        BasicIdentity basicIdentity = basicIdentityOptional.get();
        setupToken(authSupport.getOrCreateValidToken(basicIdentity.getUserName(), basicIdentity.getPassword()));
    }

    private boolean identitiesPresent(SoapHeaderElement soapHeader) {
        Optional<ServiceContext> context = soapHeaderConverter.convertSoapHeader(soapHeader);

        return context.filter(serviceContext -> Iterators.size(serviceContext.getIdentities().iterator()) > 0).isPresent();
    }

    @Override
    public boolean handleResponse(MessageContext messageContext, Object endpoint) {
        logCookieInformation();
        logPayloadMessage(RESPONSE_PAYLOAD_PREFIX, messageContext.getResponse());
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext, Object endpoint) {
        logCookieInformation();
        logPayloadMessage(FAULT_PAYLOAD_IS, messageContext.getResponse());
        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Object endpoint, Exception ex) {
    }

    public void logCookieInformation() {
        if (!log.isTraceEnabled()) {
            return;
        }
        try {
            HttpServletRequest curRequest =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            getCookieInformation(RESPONSE_PAYLOAD_PREFIX, curRequest);
        } catch (Exception e) {
            log.error("Unable to log cookie information.", e);
        }
    }

    public static String getCookieInformation(String messagePrefix, HttpServletRequest request) {
        String returnCookieString;

        HttpSession session = request.getSession();

        // find the cookie value
        String cookie = request.getHeader("Cookie");

        String decodedJSessionId = getJSessionFromCookie(cookie);

        // if the incoming cookie value is empty then we have to set a new cookie value
        if (StringUtils.isEmpty(decodedJSessionId)) {
            if (session != null) {
                returnCookieString = " Header Details - %s : %s".formatted("Set-Cookie", session.getId());
            } else {
                returnCookieString = " An inbound cookie was not found and an outbound cookie was not set";
            }
        } else if (session != null && !session.getId().equals(decodedJSessionId)) {
            returnCookieString = " An inbound cookie was present but not found. " +
                "A new cookie was generated. Inbound: %s Outbound Set-Cookie: %s".formatted(decodedJSessionId, session.getId());
        } else {
            returnCookieString = " Using the same session as the inbound cookie %s".formatted(decodedJSessionId);
        }

        return returnCookieString;
    }

    private static String getJSessionFromCookie(String cookie) {
        if (cookie == null) {
            return null;
        }
        String[] cookieParts = StringUtils.split(cookie, ";");
        for (String cookiePart : cookieParts) {
            String[] cookiePartKV = StringUtils.split(cookiePart, "=");
            if (JSESSIONID_KEY.equals(cookiePartKV[0])) {
                try {
                    return new String(Base64.getDecoder().decode(cookiePartKV[1].getBytes()));
                } catch (IllegalArgumentException e) {
                    log.error("Failed to decode the cookie value, {}", cookiePartKV[1], e);
                    return cookiePartKV[1];
                }
            }
        }
        return null;
    }

    private void logPayloadMessage(String messagePrefix, WebServiceMessage message) {
        // lets not process any of the payloads if trace level is disabled
        if (log.isTraceEnabled()) {
            try {
                Optional<ExcludePayloadLogging> excludePayloadLogging;
                if (message.getPayloadSource() instanceof DOMSource) {
                    excludePayloadLogging = logProperties.excludePayload((DOMSource) message.getPayloadSource());

                    if (excludePayloadLogging.isEmpty()) {
                        ByteArrayTransportOutputStream byteArrayTransportOutputStream =
                            new ByteArrayTransportOutputStream();
                        message.writeTo(byteArrayTransportOutputStream);

                        String payloadMessage = NEW_LINE + MESSAGE_SEPERATOR
                            + NEW_LINE + new String(byteArrayTransportOutputStream.toByteArray()) + NEW_LINE
                            + MESSAGE_SEPERATOR + NEW_LINE;

                        log.trace(messagePrefix, payloadMessage);
                    } else {
                        log.trace(
                            "REQUEST PAYLOAD. Payload was not logged as it matched the following exclusion criteria. namespace: {} root tag: {} type:{} ",
                            excludePayloadLogging.get().getNamespace(), excludePayloadLogging.get().getTag(), excludePayloadLogging.get().getType());
                    }
                } else {
                    log.warn("Could not log due to suitable xml source not be identified");
                }
            } catch (IOException ex) {
                log.error("Failed to write SOAP message", ex);
            }
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
        return ContextRegistryPayload.isApplicable(message, ContextRegistryPayload.ContextRegistryOperation.REGISTRY_OPERATION)
            || ContextRegistryPayload.isApplicable(message, ContextRegistryPayload.ContextRegistryOperation.LOOKUP_OPERATION)
            || ContextRegistryPayload.isApplicable(message, ContextRegistryPayload.ContextRegistryOperation.UNREGISTER_OPERATION);
    }
}

package uk.gov.hmcts.darts.authentication.component;

import com.google.common.collect.Iterators;
import documentum.contextreg.BasicIdentity;
import documentum.contextreg.ServiceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.soap.server.SoapEndpointInterceptor;
import uk.gov.hmcts.darts.authentication.exception.AuthenticationFailedException;
import uk.gov.hmcts.darts.authentication.exception.DocumentumUnknownTokenSoapException;
import uk.gov.hmcts.darts.authentication.exception.NoIdentitiesFoundException;
import uk.gov.hmcts.darts.cache.token.DownstreamTokenisable;
import uk.gov.hmcts.darts.cache.token.RefreshableCacheValue;
import uk.gov.hmcts.darts.cache.token.Token;
import uk.gov.hmcts.darts.cache.token.TokenRegisterable;

import java.util.Iterator;
import java.util.Optional;
import javax.xml.namespace.QName;

@Component
@RequiredArgsConstructor
@Slf4j
public class SoapRequestInterceptor implements SoapEndpointInterceptor {

    private static final String SERVICE_CONTEXT_HEADER = "{http://context.core.datamodel.fs.documentum.emc.com/}ServiceContext";
    private static final String SECURITY_HEADER = "{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Security";

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
        if (isTokenAuthentication(soapHeader)) {
            authenticateToken(soapHeader);
        } else {
            authenticateUsernameAndPassword(soapHeader);
        }

        return true;
    }

    private boolean isTokenAuthentication(SoapHeader soapHeader) {
        Iterator<SoapHeaderElement> serviceContextSoapHeaderElementIt = soapHeader.examineHeaderElements(
            QName.valueOf(SERVICE_CONTEXT_HEADER));
        return !serviceContextSoapHeaderElementIt.hasNext();
    }

    private boolean authenticateToken(SoapHeader soapHeader) {
        Iterator<SoapHeaderElement> securityToken = soapHeader.examineHeaderElements(
            QName.valueOf(SECURITY_HEADER));

        Optional<String> tokenToReturn = Optional.empty();
        if (securityToken.hasNext()) {
            SoapHeaderElement securityTokenElement = securityToken.next();
            tokenToReturn = soapHeaderConverter.convertSoapHeaderToToken(securityTokenElement);
            Token foundTokenInCache = tokenRegisterable.getToken(tokenToReturn.orElse(""));
            Optional<RefreshableCacheValue> optRefreshableCacheValue = tokenRegisterable.lookup(foundTokenInCache);

            if (optRefreshableCacheValue.isEmpty()) {
                throw new DocumentumUnknownTokenSoapException(foundTokenInCache.getToken().orElse(""));
            } else {
                if (optRefreshableCacheValue.get() instanceof uk.gov.hmcts.darts.cache.token.DownstreamTokenisable) {
                    Optional<Token> downstreamToken = ((DownstreamTokenisable)optRefreshableCacheValue.get()).getValidatedToken();

                    if (downstreamToken.isPresent()) {
                        new SecurityRequestAttributesWrapper(RequestContextHolder.currentRequestAttributes()).setAuthenticationToken(
                            downstreamToken.get().getToken().orElse(""));
                    } else {
                        throw new DocumentumUnknownTokenSoapException(foundTokenInCache.getToken().orElse(""));
                    }
                }  else {
                    new SecurityRequestAttributesWrapper(RequestContextHolder.currentRequestAttributes()).setAuthenticationToken(
                        foundTokenInCache.getToken().orElse(""));
                }
            }
        }

        if  (tokenToReturn.isEmpty()) {
            throw new DocumentumUnknownTokenSoapException("");
        }

        return true;
    }

    private boolean authenticateUsernameAndPassword(SoapHeader soapHeader) throws AuthenticationFailedException {
        Iterator<SoapHeaderElement> serviceContextSoapHeaderElementIt = soapHeader.examineHeaderElements(
            QName.valueOf(SERVICE_CONTEXT_HEADER));

        int size = Iterators.size(serviceContextSoapHeaderElementIt);
        if (size ==  1) {
            serviceContextSoapHeaderElementIt = soapHeader.examineHeaderElements(
                QName.valueOf(SERVICE_CONTEXT_HEADER));
            while (serviceContextSoapHeaderElementIt.hasNext()) {
                SoapHeaderElement soapHeaderElement = serviceContextSoapHeaderElementIt.next();
                soapHeaderConverter.convertSoapHeader(soapHeaderElement).ifPresent(serviceContext -> {

                    if (!identitiesPresent(soapHeaderElement)) {
                        throw new NoIdentitiesFoundException();
                    }

                    Optional<BasicIdentity> basicIdentityOptional = serviceContext.getIdentities()
                        .stream()
                        .filter(identity -> identity instanceof BasicIdentity)
                        .map(identity -> (BasicIdentity) identity)
                        .filter(basicIdentity -> StringUtils.isNotBlank(basicIdentity.getUserName()))
                        .filter(basicIdentity -> StringUtils.isNotBlank(basicIdentity.getPassword()))
                        .findFirst();
                    if (basicIdentityOptional.isPresent()) {
                        RefreshableCacheValue refreshableCacheValue = tokenRegisterable.createValue(serviceContext);
                        Optional<Token> token = tokenRegisterable.store(refreshableCacheValue, true);

                        if (token.isPresent()) {
                            Optional<RefreshableCacheValue> optRefreshableCacheValue = tokenRegisterable.lookup(token.get());
                            refreshableCacheValue = optRefreshableCacheValue.orElse(null);
                        }

                        if (token.isPresent() && refreshableCacheValue instanceof uk.gov.hmcts.darts.cache.token.DownstreamTokenisable) {
                            Optional<Token> tokenDownstream = ((DownstreamTokenisable) refreshableCacheValue).getValidatedToken();
                            if (tokenDownstream.isEmpty()) {
                                throw new AuthenticationFailedException();
                            } else {
                                new SecurityRequestAttributesWrapper(RequestContextHolder.currentRequestAttributes()).setAuthenticationToken(
                                    tokenDownstream.get().getToken().orElse(""));
                            }
                        } else if (!token.isPresent()) {
                            throw new AuthenticationFailedException();
                        } else {
                            new SecurityRequestAttributesWrapper(RequestContextHolder.currentRequestAttributes()).setAuthenticationToken(
                                token.get().getToken().orElse(""));
                        }
                    } else  {
                        throw new NoIdentitiesFoundException();
                    }
                });
            }
        }  else {
            throw new NoIdentitiesFoundException();
        }
        return true;
    }


    private boolean identitiesPresent(SoapHeaderElement soapHeader) {
        Optional<ServiceContext> context = soapHeaderConverter.convertSoapHeader(soapHeader);

        return context.filter(serviceContext -> Iterators.size(serviceContext.getIdentities().iterator()) > 0).isPresent();
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

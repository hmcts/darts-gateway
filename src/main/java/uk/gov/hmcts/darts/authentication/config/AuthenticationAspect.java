package uk.gov.hmcts.darts.authentication.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.ws.soap.SoapHeaderElement;
import uk.gov.hmcts.darts.authentication.component.SoapHeaderConverter;
import uk.gov.hmcts.darts.cache.token.TokenRegisterable;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

@Aspect
@Configuration
@RequiredArgsConstructor
@Slf4j
public class AuthenticationAspect {

    private static final String ACCESS_TOKEN_REQUEST_ATTR = "access_token";

    private final SoapHeaderConverter soapHeaderConverter;
    private final TokenRegisterable tokenRegisterable;

    @Pointcut("within(uk.gov.hmcts.darts.ws.DartsEndpoint)")
    public void withinDartsEndpointPointcut() {
    }

    @Before("withinDartsEndpointPointcut()")
    public void handleSoapHeaderAuthenticationAdvice(JoinPoint joinPoint) {
        Optional<Object> soapHeaderOptional = Arrays.stream(joinPoint.getArgs())
            .filter(soapHeader -> soapHeader instanceof SoapHeaderElement)
            .findFirst();

        soapHeaderOptional.ifPresent(soapHeaderElement -> soapHeaderConverter.convertSoapHeader((SoapHeaderElement) soapHeaderElement)
            .ifPresent(serviceContext -> tokenRegisterable.createToken(serviceContext)
                .ifPresent(cacheToken -> RequestContextHolder.currentRequestAttributes().setAttribute(
                               ACCESS_TOKEN_REQUEST_ATTR,
                               cacheToken.getToken(),
                               SCOPE_REQUEST
                           )
                )
            )
        );
    }

}

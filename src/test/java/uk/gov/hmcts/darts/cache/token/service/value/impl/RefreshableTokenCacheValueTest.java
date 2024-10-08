package uk.gov.hmcts.darts.cache.token.service.value.impl;

import documentum.contextreg.ServiceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.xml.transform.StringSource;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;
import uk.gov.hmcts.darts.cache.token.exception.CacheTokenCreationException;
import uk.gov.hmcts.darts.cache.token.service.Token;
import uk.gov.hmcts.darts.cache.token.service.TokenGeneratable;
import uk.gov.hmcts.darts.cache.token.service.TokenRegisterable;

import static org.mockito.Mockito.when;

class RefreshableTokenCacheValueTest {
    private static final String SERVICE_CONTEXT_UNDER_TEST = """
      <context xmlns:ns4="http://profiles.core.datamodel.fs.documentum.emc.com/"    xmlns:ns2="http://context.core.datamodel.fs.documentum.emc.com/" xmlns:ns3="http://properties.core.datamodel.fs.documentum.emc.com/" ><ns2:Identities
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="ns2:RepositoryIdentity" repositoryName="moj_darts" password="${PASSWORD}" userName="${USER}">
        </ns2:Identities>
        <ns2:Profiles
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="ns4:ContentTransferProfile" isProcessOLELinks="false" allowAsyncContentTransfer="false" allowCachedContentTransfer="false" transferMode="MTOM">
        </ns2:Profiles>
      </context>
        """;

    private ServiceContext context;

    private Token token;

    private Token replaceToken;

    private static final String CACHED_TOKEN_STRING = "MyToken";

    private static final String REPLACE_TOKEN_STRING = "MyTokenReplace";

    @BeforeAll
    static void beforeAll() {

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);

        MockedStatic<RequestContextHolder> requestContextHolder = Mockito.mockStatic(RequestContextHolder.class);
        ServletRequestAttributes requestAttributes = Mockito.mock(ServletRequestAttributes.class);

        requestContextHolder.when(RequestContextHolder::currentRequestAttributes).thenReturn(requestAttributes);
        when(requestAttributes.getRequest()).thenReturn(request);

        String sessionId = "sessionId";
        when(request.getSession(false)).thenReturn(null);
        when(request.getSession()).thenReturn(session);
        when(session.getId()).thenReturn(sessionId);
    }

    @BeforeEach
    void beforeTest() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ServiceContext.class);
        Unmarshaller jaxbMarshaller = jaxbContext.createUnmarshaller();
        StringSource ss = new StringSource(SERVICE_CONTEXT_UNDER_TEST);
        context = jaxbMarshaller.unmarshal(ss, ServiceContext.class).getValue();

        TokenValidator validate = Mockito.mock(TokenValidator.class);
        when(validate.test(Mockito.eq(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY), Mockito.notNull())).thenReturn(true);
        token = Token.readToken(CACHED_TOKEN_STRING, false, validate);
        replaceToken = Token.readToken(REPLACE_TOKEN_STRING, false, validate);
    }

    @Test
    void testBasicParsing() {
        TokenGeneratable generatable = Mockito.mock(TokenGeneratable.class);
        when(generatable.createToken(Mockito.notNull())).thenReturn(token);

        // run test
        RefeshableTokenCacheValue contextCacheValue = new RefeshableTokenCacheValue(context, generatable);
        Assertions.assertFalse(contextCacheValue.getContextString().isEmpty());
        Assertions.assertEquals(TokenRegisterable.CACHE_PREFIX + ":" + "${USER}:${PASSWORD}", contextCacheValue.getSharedKey());
        Assertions.assertEquals(CACHED_TOKEN_STRING, contextCacheValue.getTokenString());
    }

    @Test
    void testNeedRefreshing() {
        TokenGeneratable generatable = Mockito.mock(TokenGeneratable.class);
        when(generatable.getToken(CACHED_TOKEN_STRING)).thenReturn(token);
        when(generatable.createToken(Mockito.notNull())).thenReturn(token);

        // run test
        RefeshableTokenCacheValue contextCacheValue = new RefeshableTokenCacheValue(context, generatable);
        Assertions.assertFalse(contextCacheValue.getContextString().isEmpty());
        Assertions.assertEquals(TokenRegisterable.CACHE_PREFIX + ":" + "${USER}:${PASSWORD}", contextCacheValue.getSharedKey());
        Assertions.assertEquals(CACHED_TOKEN_STRING, contextCacheValue.getTokenString());

        Assertions.assertFalse(contextCacheValue.doesRequireRefresh());
    }

    @Test
    void testRefreshToken() {
        TokenGeneratable generatable = Mockito.mock(TokenGeneratable.class);
        when(generatable.createToken(context)).thenReturn(token);
        when(generatable.getToken(CACHED_TOKEN_STRING)).thenReturn(token);
        when(generatable.createToken(Mockito.notNull())).thenReturn(token, replaceToken);

        // run test
        RefeshableTokenCacheValue contextCacheValue = new RefeshableTokenCacheValue(context, generatable);
        Assertions.assertFalse(contextCacheValue.getContextString().isEmpty());
        Assertions.assertEquals(TokenRegisterable.CACHE_PREFIX + ":" + "${USER}:${PASSWORD}", contextCacheValue.getSharedKey());
        Assertions.assertEquals(CACHED_TOKEN_STRING, contextCacheValue.getTokenString());
        Assertions.assertDoesNotThrow(contextCacheValue::performRefresh);
        Assertions.assertEquals(REPLACE_TOKEN_STRING, contextCacheValue.getTokenString());
    }

    @Test
    void testRefreshTokenFailure() {
        TokenGeneratable generatable = Mockito.mock(TokenGeneratable.class);
        when(generatable.getToken(CACHED_TOKEN_STRING)).thenReturn(token);
        when(generatable.createToken(Mockito.notNull())).thenReturn(token).thenThrow(new CacheTokenCreationException(""));

        // run test
        RefeshableTokenCacheValue contextCacheValue = new RefeshableTokenCacheValue(context, generatable);
        Assertions.assertFalse(contextCacheValue.getContextString().isEmpty());
        Assertions.assertEquals(TokenRegisterable.CACHE_PREFIX + ":" + "${USER}:${PASSWORD}", contextCacheValue.getSharedKey());
        Assertions.assertEquals(CACHED_TOKEN_STRING, contextCacheValue.getTokenString());
        contextCacheValue.performRefresh();
        Assertions.assertEquals(CACHED_TOKEN_STRING, contextCacheValue.getTokenString());
    }
}

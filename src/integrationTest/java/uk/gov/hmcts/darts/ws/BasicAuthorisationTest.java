package uk.gov.hmcts.darts.ws;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.hmcts.darts.cache.AuthSupport;
import uk.gov.hmcts.darts.cache.token.component.TokenGenerator;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;
import uk.gov.hmcts.darts.common.utils.TestUtils;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClient;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClientProvider;
import uk.gov.hmcts.darts.common.utils.client.darts.DartsClientProvider;
import uk.gov.hmcts.darts.common.utils.client.darts.DartsGatewayClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("int-test-jwt-token-shared")
class BasicAuthorisationTest extends ContextRegistryParent {

    private static final String CONTEXT_REGISTRY_TOKEN = "testToken";

    @MockitoBean
    private TokenGenerator mockOauthTokenGenerator;

    @Autowired
    private CacheProperties properties;

    @BeforeEach
    public void before() {
        doReturn(DEFAULT_TOKEN).when(authSupport).getOrCreateValidToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        doReturn(DEFAULT_TOKEN).when(authSupport).getOrCreateValidToken("not_whitelisted_service", DEFAULT_HEADER_PASSWORD);
        doReturn(CONTEXT_REGISTRY_TOKEN).when(authSupport).getOrCreateValidToken(SERVICE_CONTEXT_USER, SERVICE_CONTEXT_PASSWORD);
        doReturn(DEFAULT_TOKEN).when(authSupport).getOrCreateValidToken(SERVICE_CONTEXT_USER, SERVICE_CONTEXT_USER);

        doNothing().when(authSupport).validateToken(DEFAULT_TOKEN);
        doNothing().when(authSupport).validateToken(CONTEXT_REGISTRY_TOKEN);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testBasicAuthorisationRequestFromNotWhitelistedServiceSucceedsForContextRegistryCall(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeHandleRegister(client);
        }, "not_whitelisted_service", DEFAULT_HEADER_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testBasicAuthorisationRequestFromNotWhitelistedServiceFails(DartsGatewayClient client) throws Exception {

        authenticationStub.assertFailsWithServiceAuthorisationFailedError(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile("payloads/getCases/soapRequest.xml");

            client.getCases(getGatewayUri(), soapRequestStr);
        }, "not_whitelisted_service", DEFAULT_HEADER_PASSWORD, null);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testBasicAuthorisationRequestFromNotWhitelistedServiceSucceeds(DartsGatewayClient client) throws Exception {

        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile("payloads/getCases/soapRequest.xml");
            String dartsApiResponseStr = TestUtils.getContentsFromFile("payloads/getCases/dartsApiResponse.json");

            stubFor(get(urlPathEqualTo("/cases"))
                        .willReturn(aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(dartsApiResponseStr)));


            client.getCases(getGatewayUri(), soapRequestStr);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        verify(authSupport).getOrCreateValidToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        WireMock.verify(getRequestedFor(urlPathEqualTo("/cases"))
                            .withHeader("Authorization", new RegexPattern("Bearer test")));
    }

}

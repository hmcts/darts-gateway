package uk.gov.hmcts.darts.log;


import com.service.mojdarts.synapps.com.AddDocumentResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.darts.authentication.component.SoapRequestInterceptor;
import uk.gov.hmcts.darts.cache.token.component.TokenGenerator;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;
import uk.gov.hmcts.darts.cache.token.service.Token;
import uk.gov.hmcts.darts.common.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.common.utils.client.darts.DartsClientProvider;
import uk.gov.hmcts.darts.common.utils.client.darts.DartsGatewayClient;
import uk.gov.hmcts.darts.testutils.IntegrationBase;
import uk.gov.hmcts.darts.ws.ContextRegistryParent;

import java.nio.charset.Charset;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles({"int-test-jwt-token-shared", "no-payload-log-exclusions"})
class EventWebServiceLoggingTest extends IntegrationBase {

    private @Value("classpath:payloads/events/valid-dailyList.xml") Resource validDlEvent;
    private @Value("classpath:payloads/events/valid-event-response.xml") Resource validDlEventResponse;
    @MockBean
    private TokenGenerator mockOauthTokenGenerator;

    @MockBean
    private TokenValidator tokenValidator;

    @BeforeEach
    public void before() {
        when(tokenValidator.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq("test"))).thenReturn(true);
        when(tokenValidator.test(Mockito.eq(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq("test"))).thenReturn(true);

        when(mockOauthTokenGenerator.acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD))
            .thenReturn("test");

        when(mockOauthTokenGenerator.acquireNewToken(ContextRegistryParent.SERVICE_CONTEXT_USER, ContextRegistryParent.SERVICE_CONTEXT_PASSWORD))
            .thenReturn("test");
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesValidDailyListPayload(
        DartsGatewayClient client
    ) throws Exception {

        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            dailyListApiStub.willRespondSuccessfully();

            SoapAssertionUtil<AddDocumentResponse> response = client.addDocument(
                getGatewayUri(),
                validDlEvent.getContentAsString(Charset.defaultCharset())
            );
            response.assertIdenticalResponse(client.convertData(
                validDlEventResponse.getContentAsString(Charset.defaultCharset()),
                AddDocumentResponse.class
            ).getValue());

            dailyListApiStub.verifyPostRequest();

            // ensure that the payload logging is turned off for this api call
            // ensure that the payload logging is turned on for this api call
            Assertions.assertFalse(logAppender.searchLogs(SoapRequestInterceptor.REQUEST_PAYLOAD_PREFIX, null, null).isEmpty());
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        dailyListApiStub.verifyPostRequest();
        dailyListApiStub.verifyPatchRequest();

        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }
}

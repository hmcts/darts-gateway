package uk.gov.hmcts.darts;

import documentum.contextreg.Register;
import documentum.contextreg.RegisterResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import uk.gov.hmcts.darts.common.client.ContextRegistryClientWrapper;
import uk.gov.hmcts.darts.common.payload.SubstitutablePayload;
import uk.gov.hmcts.darts.common.payload.SubstituteKey;
import uk.gov.hmcts.darts.common.utils.client.SoapAssertionUtil;

import static uk.gov.hmcts.darts.common.configuration.ContextClientConfiguration.WEB_CONTEXT;

/**
 * Performance of the context registry is vital as it is used across all of our integration endpoints in order to
 * authenticate requests as well as talk to the downstream darts api.
 *
 * <p>This class utilises in memory jmeter to perform a baseline set of performance test against the context registry
 * of a single gateway deployment running on my local machine.
 * The asserted timings can be tailored according to the expected functional test infrastructure we are running against.
 *
 * <p>These tests act as a good indicator as to whether the current context registry performance is in line with our baseline metrics.
 * They also act as a nice early warning system as to any  regressions in performance.
 *
 * <p>NOTE: This test does NOT act as a substitute for running performance tests within an official performance test environment.
 */
class ContextRegistryPerformanceTest extends FunctionalPerformanceTestBase {

    private static final int SINGLE_REQUEST_TIME_MILLIS_SECONDARY_TOKEN = 850;

    private static final int SINGLE_REQUEST_TIME_MILLIS_INITIAL_TOKEN = 2300;

    @Test
    void testPerformanceOfSingleRegisterRequestWithNewToken() throws Exception {
        SubstitutablePayload substitutablePayload = new SubstitutablePayload("soapRegisterFull.xml");
        String body = substitutablePayload
            .setSubstituteValue(SubstituteKey.USER_NAME, xhibit.getExternalUserToInternalUserMapping().getUserName())
            .setSubstituteValue(SubstituteKey.PASSWORD, xhibit.getExternalUserToInternalUserMapping().getExternalPassword())
            .substitute();

        testSendPerformanceTest(1, 1, SINGLE_REQUEST_TIME_MILLIS_INITIAL_TOKEN, body, WEB_CONTEXT);
    }

    @Test
    void testAPerformanceFailure() throws Exception {
        SubstitutablePayload substitutablePayload = new SubstitutablePayload("soapRegisterFull.xml");
        String body = substitutablePayload
            .setSubstituteValue(SubstituteKey.USER_NAME, xhibit.getExternalUserToInternalUserMapping().getUserName())
            .setSubstituteValue(SubstituteKey.PASSWORD, xhibit.getExternalUserToInternalUserMapping().getExternalPassword())
            .substitute();

        Assertions.assertThrows(AssertionFailedError.class, () -> {
            testSendPerformanceTest(1, 1, 1, body, WEB_CONTEXT);
        });
    }

    @Test
    void testPerformanceOfSingleRegisterRequestWithNewTokenAndRequestOfToken() throws Exception {
        SubstitutablePayload substitutablePayload = new SubstitutablePayload("soapRegisterFull.xml");
        String body = substitutablePayload
            .setSubstituteValue(SubstituteKey.USER_NAME, xhibit.getExternalUserToInternalUserMapping().getUserName())
            .setSubstituteValue(SubstituteKey.PASSWORD, xhibit.getExternalUserToInternalUserMapping().getExternalPassword())
            .substitute();

        // first token is going to be slower
        testSendPerformanceTest(1, 1, SINGLE_REQUEST_TIME_MILLIS_INITIAL_TOKEN, body, WEB_CONTEXT);

        // then should be 0.4 milli seconds
        testSendPerformanceTest(1, 1, SINGLE_REQUEST_TIME_MILLIS_SECONDARY_TOKEN, body, WEB_CONTEXT);
    }

    @Test
    void testPerformanceOf10RegisterRequestSWithNewTokenAndRequestOfToken() throws Exception {
        SubstitutablePayload substitutablePayload = new SubstitutablePayload("soapRegisterFull.xml");
        String body = substitutablePayload
            .setSubstituteValue(SubstituteKey.USER_NAME, xhibit.getExternalUserToInternalUserMapping().getUserName())
            .setSubstituteValue(SubstituteKey.PASSWORD, xhibit.getExternalUserToInternalUserMapping().getExternalPassword())
            .substitute();

        // first token is going to be slower
        testSendPerformanceTest(1, 1, SINGLE_REQUEST_TIME_MILLIS_INITIAL_TOKEN, body, WEB_CONTEXT);

        // then should be 0.4 milli seconds
        testSendPerformanceTest(10, 1,2700,  body, WEB_CONTEXT);
    }

    @Test
    void testPerformanceOf50RegisterRequestsWithNewTokenAndRequestOfToken() throws Exception {
        SubstitutablePayload substitutablePayload = new SubstitutablePayload("soapRegisterFull.xml");
        String body = substitutablePayload
            .setSubstituteValue(SubstituteKey.USER_NAME, xhibit.getExternalUserToInternalUserMapping().getUserName())
            .setSubstituteValue(SubstituteKey.PASSWORD, xhibit.getExternalUserToInternalUserMapping().getExternalPassword())
            .substitute();

        // first token is going to be slower
        testSendPerformanceTest(1, 1, SINGLE_REQUEST_TIME_MILLIS_INITIAL_TOKEN, body, WEB_CONTEXT);

        // then should be 0.4 milli seconds
        testSendPerformanceTest(50, 1,82_000,  body, WEB_CONTEXT);
    }

    @Test
    void testPerformanceOfSingleLookupWithNewToken() throws Exception {
        Register register = ContextRegistryClientWrapper.getRegisterPayload();

        SoapAssertionUtil<RegisterResponse> soapAssertionUtil = xhibit.register(register);

        SubstitutablePayload substitutablePayload = new SubstitutablePayload("soapLookupFull.xml");
        String body = substitutablePayload
            .setSubstituteValue(SubstituteKey.USER_NAME, xhibit.getExternalUserToInternalUserMapping().getUserName())
            .setSubstituteValue(SubstituteKey.PASSWORD, xhibit.getExternalUserToInternalUserMapping().getExternalPassword())
            .setSubstituteValue(SubstituteKey.TOKEN, soapAssertionUtil.getResponse().getValue().getReturn())
            .substitute();

        testSendPerformanceTest(1, 1,850,  body, WEB_CONTEXT);
    }

    @Test
    void testPerformanceOf10LookupWithNewToken() throws Exception {
        Register register = ContextRegistryClientWrapper.getRegisterPayload();

        SoapAssertionUtil<RegisterResponse> soapAssertionUtil = xhibit.register(register);
        xhibit.setToken(soapAssertionUtil.getResponse().getValue().getReturn());

        SubstitutablePayload substitutablePayload = new SubstitutablePayload("soapLookupFull.xml");
        String body = substitutablePayload
            .setSubstituteValue(SubstituteKey.USER_NAME, xhibit.getExternalUserToInternalUserMapping().getUserName())
            .setSubstituteValue(SubstituteKey.PASSWORD, xhibit.getExternalUserToInternalUserMapping().getExternalPassword())
            .setSubstituteValue(SubstituteKey.TOKEN, soapAssertionUtil.getResponse().getValue().getReturn())
            .substitute();
        testSendPerformanceTest(10, 1,2700,  body, WEB_CONTEXT);
    }
}

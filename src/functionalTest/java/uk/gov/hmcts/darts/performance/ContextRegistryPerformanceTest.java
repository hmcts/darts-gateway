package uk.gov.hmcts.darts.performance;

import documentum.contextreg.Register;
import documentum.contextreg.RegisterResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
@Slf4j
class ContextRegistryPerformanceTest extends FunctionalPerformanceTestBase {

    private static final int SINGLE_REQUEST_TIME_MILLIS_SECONDARY_TOKEN = 65;

    private static final int SINGLE_REQUEST_TIME_MILLIS_INITIAL_TOKEN = 3300;

    private static final int SINGLE_LOOKUP_MILLIS = 31;

    private static final int SINGLE_BENCHMARK_MILLIS = 400;

    private static final int SINGLE_BENCHMARK_CONCURRENT_THREADS = 120;

    private SoapAssertionUtil<RegisterResponse> soapAssertionUtil;

    @BeforeEach
    @SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.DoNotUseThreads"})
    public void before() throws Exception {
        // wait 5 seconds to give the gateway a break
        Thread.sleep(5000);

        Register register = ContextRegistryClientWrapper.getRegisterPayload();

        soapAssertionUtil = xhibit.register(register);
        xhibit.setToken(soapAssertionUtil.getResponse().getValue().getReturn());
    }

    @Test
    void testPerformanceOfSingleRegisterRequestWithNewToken() throws Exception {
        SubstitutablePayload substitutablePayload = new SubstitutablePayload("soapRegisterFull.xml");
        String body = substitutablePayload
            .setSubstituteValue(SubstituteKey.USER_NAME, xhibit.getExternalUserToInternalUserMapping().getUserName())
            .setSubstituteValue(SubstituteKey.PASSWORD, xhibit.getExternalUserToInternalUserMapping().getExternalPassword())
            .substitute();

        testSendPerformanceTest(1, 1, SINGLE_REQUEST_TIME_MILLIS_SECONDARY_TOKEN, body, WEB_CONTEXT);
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

        clean();

        // first token is going to be slower
        testSendPerformanceTest(1, 1, SINGLE_REQUEST_TIME_MILLIS_INITIAL_TOKEN, body, WEB_CONTEXT);

        // then should be much faster
        testSendPerformanceTest(1, 1, SINGLE_REQUEST_TIME_MILLIS_SECONDARY_TOKEN, body, WEB_CONTEXT);
    }

    @Test
    void testPerformanceOf4RegisterRequestsWithNewTokenAndRequestOfToken() throws Exception {
        SubstitutablePayload substitutablePayload = new SubstitutablePayload("soapRegisterFull.xml");
        String body = substitutablePayload
            .setSubstituteValue(SubstituteKey.USER_NAME, xhibit.getExternalUserToInternalUserMapping().getUserName())
            .setSubstituteValue(SubstituteKey.PASSWORD, xhibit.getExternalUserToInternalUserMapping().getExternalPassword())
            .substitute();

        // then should be much faster
        testSendPerformanceTest(4, 1,SINGLE_REQUEST_TIME_MILLIS_SECONDARY_TOKEN,  body, WEB_CONTEXT);
    }

    @Test
    void testPerformanceOfSingleLookupWithNewToken() throws Exception {
        SubstitutablePayload substitutablePayload = new SubstitutablePayload("soapLookupFull.xml");
        String body = substitutablePayload
            .setSubstituteValue(SubstituteKey.USER_NAME, xhibit.getExternalUserToInternalUserMapping().getUserName())
            .setSubstituteValue(SubstituteKey.PASSWORD, xhibit.getExternalUserToInternalUserMapping().getExternalPassword())
            .setSubstituteValue(SubstituteKey.TOKEN, soapAssertionUtil.getResponse().getValue().getReturn())
            .substitute();

        testSendPerformanceTest(1, 1,SINGLE_LOOKUP_MILLIS,  body, WEB_CONTEXT);
    }

    @Test
    void testPerformanceOf20LookupWithNewToken() throws Exception {

        SubstitutablePayload substitutablePayload = new SubstitutablePayload("soapLookupFull.xml");
        String body = substitutablePayload
            .setSubstituteValue(SubstituteKey.USER_NAME, xhibit.getExternalUserToInternalUserMapping().getUserName())
            .setSubstituteValue(SubstituteKey.PASSWORD, xhibit.getExternalUserToInternalUserMapping().getExternalPassword())
            .setSubstituteValue(SubstituteKey.TOKEN, soapAssertionUtil.getResponse().getValue().getReturn())
            .substitute();
        testSendPerformanceTest(20, 1,SINGLE_LOOKUP_MILLIS,  body, WEB_CONTEXT);
    }

    @Test
    void testBenchmarkPerformanceLookupWithNewToken() throws Exception {

        SubstitutablePayload substitutablePayload = new SubstitutablePayload("soapLookupFull.xml");
        String body = substitutablePayload
            .setSubstituteValue(SubstituteKey.USER_NAME, xhibit.getExternalUserToInternalUserMapping().getUserName())
            .setSubstituteValue(SubstituteKey.PASSWORD, xhibit.getExternalUserToInternalUserMapping().getExternalPassword())
            .setSubstituteValue(SubstituteKey.TOKEN, soapAssertionUtil.getResponse().getValue().getReturn())
            .substitute();
        testSendPerformanceTest(SINGLE_BENCHMARK_CONCURRENT_THREADS, 1, SINGLE_BENCHMARK_MILLIS,  body, WEB_CONTEXT);
    }

    @Test
    void testBenchmarkPerformanceOfSingleRegisterRequestWithToken() throws Exception {
        SubstitutablePayload substitutablePayload = new SubstitutablePayload("soapRegisterFull.xml");
        String body = substitutablePayload
            .setSubstituteValue(SubstituteKey.USER_NAME, xhibit.getExternalUserToInternalUserMapping().getUserName())
            .setSubstituteValue(SubstituteKey.PASSWORD, xhibit.getExternalUserToInternalUserMapping().getExternalPassword())
            .substitute();

        // then should be much faster
        testSendPerformanceTest(SINGLE_BENCHMARK_CONCURRENT_THREADS, 1, SINGLE_BENCHMARK_MILLIS, body, WEB_CONTEXT);
    }
}
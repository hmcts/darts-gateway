package uk.gov.hmcts.darts;

import documentum.contextreg.Register;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import uk.gov.hmcts.darts.common.client.ContextRegistryClientWrapper;
import uk.gov.hmcts.darts.common.utils.TestUtils;

import java.util.HashMap;
import java.util.Map;

class TestContextRegistryPerformance extends FunctionalPerformanceTest {

    @Test
    void testPerformanceOfSingleRequestWithNewToken() throws Exception {
        Map<String, String> subsValues = new HashMap<>();
        subsValues.put("${USER}", xhibit.getExternalUserToInternalUserMapping().getUserName());
        subsValues.put("${PASSWORD}", xhibit.getExternalUserToInternalUserMapping().getExternalPassword());

        String body = TestUtils.getContentsFromFile("soapRegisterFull.xml", subsValues);

        testSendPerformanceTest(1, 1,1500,  body, GATEWAY_WEB_CONTEXT);
    }

    @Test
    void testAPerformanceFailure() throws Exception {
        Map<String, String> subsValues = new HashMap<>();
        subsValues.put("${USER}", xhibit.getExternalUserToInternalUserMapping().getUserName());
        subsValues.put("${PASSWORD}", xhibit.getExternalUserToInternalUserMapping().getExternalPassword());

        String body = TestUtils.getContentsFromFile("soapRegisterFull.xml", subsValues);

        Assertions.assertThrows(AssertionFailedError.class, () -> {
            testSendPerformanceTest(1, 1, 1, body, GATEWAY_WEB_CONTEXT);
        });
    }

    @Test
    void testPerformanceOfSingleRequestWithNewTokenAndRequestOfToken() throws Exception {
        Map<String, String> subsValues = new HashMap<>();
        subsValues.put("${USER}", xhibit.getExternalUserToInternalUserMapping().getUserName());
        subsValues.put("${PASSWORD}", xhibit.getExternalUserToInternalUserMapping().getExternalPassword());

        String body = TestUtils.getContentsFromFile("soapRegisterFull.xml", subsValues);
        Register register = ContextRegistryClientWrapper.getRegisterPayload();

        // first token is going to be slower
        testSendPerformanceTest(1, 1,2300,  body, GATEWAY_WEB_CONTEXT);

        // then should be below 0.4 milli seconds
        testSendPerformanceTest(1, 1,400,  body, GATEWAY_WEB_CONTEXT);
    }

    @Test
    void testPerformanceOf10RequestSWithNewTokenAndRequestOfToken() throws Exception {
        Map<String, String> subsValues = new HashMap<>();
        subsValues.put("${USER}", xhibit.getExternalUserToInternalUserMapping().getUserName());
        subsValues.put("${PASSWORD}", xhibit.getExternalUserToInternalUserMapping().getExternalPassword());

        String body = TestUtils.getContentsFromFile("soapRegisterFull.xml", subsValues);

        // first token is going to be slower
        testSendPerformanceTest(1, 1,2300,  body, GATEWAY_WEB_CONTEXT);

        // then should be below 0.4 milli seconds
        testSendPerformanceTest(10, 1,400,  body, GATEWAY_WEB_CONTEXT);
    }

    @Test
    void testPerformanceOf50RequestsWithNewTokenAndRequestOfToken() throws Exception {
        Map<String, String> subsValues = new HashMap<>();
        subsValues.put("${USER}", xhibit.getExternalUserToInternalUserMapping().getUserName());
        subsValues.put("${PASSWORD}", xhibit.getExternalUserToInternalUserMapping().getExternalPassword());

        String body = TestUtils.getContentsFromFile("soapRegisterFull.xml", subsValues);

        // first token is going to be slower
        testSendPerformanceTest(1, 1,2300,  body, GATEWAY_WEB_CONTEXT);

        // then should be below 0.4 milli seconds
        testSendPerformanceTest(50, 1,400,  body, GATEWAY_WEB_CONTEXT);
    }

    @Test
    void testPerformanceOf75RequestsWithNewTokenAndRequestOfToken() throws Exception {
        Map<String, String> subsValues = new HashMap<>();
        subsValues.put("${USER}", xhibit.getExternalUserToInternalUserMapping().getUserName());
        subsValues.put("${PASSWORD}", xhibit.getExternalUserToInternalUserMapping().getExternalPassword());

        String body = TestUtils.getContentsFromFile("soapRegisterFull.xml", subsValues);

        // first token is going to be slower
        testSendPerformanceTest(1, 1,2300,  body, GATEWAY_WEB_CONTEXT);

        // then should be below 0.4 milli seconds
        testSendPerformanceTest(50, 1,400,  body, GATEWAY_WEB_CONTEXT);
    }

    @Test
    void testPerformanceOf100RequestsWithNewTokenAndRequestOfToken() throws Exception {
        Map<String, String> subsValues = new HashMap<>();
        subsValues.put("${USER}", xhibit.getExternalUserToInternalUserMapping().getUserName());
        subsValues.put("${PASSWORD}", xhibit.getExternalUserToInternalUserMapping().getExternalPassword());

        String body = TestUtils.getContentsFromFile("soapRegisterFull.xml", subsValues);

        // first token is going to be slower
        testSendPerformanceTest(1, 1,2300,  body, GATEWAY_WEB_CONTEXT);

        // then should be below 0.4 milli seconds
        testSendPerformanceTest(100, 1,600,  body, GATEWAY_WEB_CONTEXT);
    }
}

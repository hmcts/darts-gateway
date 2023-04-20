package uk.gov.hmcts.darts.apim.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("int-test")
class GatewayControllerIntTest {

    private static final String RESPONSE_NAME_VALID = "valid";
    private static final String RESPONSE_NAME_ERROR = "error";

    @Autowired
    MockMvc mockMvc;

    @Test
    void testValidateContentValid() {
        String content = """
            <root>
                <content>some good content</content>
            </root>
            """;

        assertValidate(content, OK.value(),true, "");
    }

    @Test
    void testValidateContentInvalidThreateningContent() {
        String content = """
            <root>
                <content>some bad,, content</content>
            </root>
            """;

        assertValidate(content, OK.value(),false, "Text contains double comma");
    }

    @Test
    void testValidateContentInvalidXmlComplexity() {
        String content = """
            <root>
                <content1/>
                <content2/>
                <content3/>
                <content4/>
            </root>
            """;
        assertValidate(content, OK.value(),false, "Content fails XML complexity check");
    }

    @Test
    void testValdateContentXmlConversionException() {
        String content = "not XML";

        assertValidate(content, BAD_REQUEST.value(),false, "Error converting text to XML");
    }

    private MvcResult invokeValidate(String requestContent, int expectedStatus) {
        MvcResult mvcResult = null;

        try {
            mvcResult = mockMvc.perform(post("/validate").header(
                HttpHeaders.CONTENT_TYPE,
                MediaType.TEXT_XML_VALUE
            ).content(requestContent)).andExpect(status().is(expectedStatus)).andReturn();
        } catch (Exception e) {
            fail("Exception occurred when invoking GatewayController validate method");
        }

        return mvcResult;
    }

    private void assertValidate(String requestContent,
                                int expectedStatus,
                                boolean expectedValidState,
                                String expectedError) {
        MvcResult mvcResult = invokeValidate(requestContent, expectedStatus);
        assertNotNull(mvcResult, "Result should not be null");

        try {
            MockHttpServletResponse response = mvcResult.getResponse();
            assertNotNull(response, "Result response should not be null");
            assertEquals(
                MediaType.APPLICATION_JSON_VALUE,
                response.getHeader(HttpHeaders.CONTENT_TYPE),
                "Result response has unexpected content type"
            );

            String responseBody = mvcResult.getResponse().getContentAsString();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            JsonNode validNode = jsonNode.get(RESPONSE_NAME_VALID);
            assertEquals(
                expectedValidState,
                validNode.booleanValue(),
                "Result response does not have expected valid state"
            );

            JsonNode errorNode = jsonNode.get(RESPONSE_NAME_ERROR);
            assertEquals(expectedError, errorNode.textValue(), "Result response does not have expected error message");
        } catch (UnsupportedEncodingException e) {
            fail("Could not get result response content");
        } catch (JsonProcessingException e) {
            fail("Could not create Json document from result response");
        }
    }
}

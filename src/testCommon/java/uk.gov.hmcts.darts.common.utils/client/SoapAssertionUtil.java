package uk.gov.hmcts.darts.common.utils.client;

import com.emc.documentum.fs.rt.ServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synapps.moj.dfs.response.DARTSResponse;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import lombok.Getter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.springframework.util.ReflectionUtils;
import org.springframework.xml.transform.StringSource;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.io.StringWriter;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.fail;

@Getter
public class SoapAssertionUtil<O> {

    private final JAXBElement<O> response;

    public SoapAssertionUtil(JAXBElement<O> response) {
        this.response = response;
    }

    public void assertIdenticalResponse(O assertion)
        throws JsonProcessingException {


        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(assertion);
        String jsonToCompare = mapper.writeValueAsString(response.getValue());

        Assertions.assertEquals(json, jsonToCompare, "Expected web service response to be equal");
    }

    public static void assertErrorResponse(String code, String message, DARTSResponse response) {
        Assertions.assertEquals(code, response.getCode(), "Expected code to be equal");
        Assertions.assertEquals(message, response.getMessage(), "Expected message to be equal");
    }

    @SneakyThrows
    public <T extends DARTSResponse> void assertCodeAndMessage(CodeAndMessage codeAndMessage) {
        Object value = response.getValue();
        if (value instanceof JAXBElement<?> jaxbElement) {
            value = jaxbElement.getValue();
        }

        Method method = ReflectionUtils.findMethod(value.getClass(), "getReturn");
        if (method != null) {
            value = method.invoke(value);
        }

        if (value instanceof DARTSResponse dartsResponse) {
            assertErrorResponse(codeAndMessage.getCode(), codeAndMessage.getMessage(), dartsResponse);
            return;
        }
        fail("Expected response to be of type DARTSResponse but was: " + value.getClass().getName());
    }

    public void assertIdenticalErrorResponseXml(String xml, Class<O> clazz) {
        assertIdenticalErrorResponseXml(xml, clazz, true);
    }

    public void assertIdenticalErrorResponseXml(String xml, Class<O> clazz, boolean removeStackTrace) {
        String actualXml = responseAsXml();
        String expectedXml = responseAsXml(toXml(xml, clazz));
        //Stack trace can change hash numbers and cause the test to fail so we may want to remove on occation
        if (removeStackTrace) {
            actualXml = actualXml.substring(0, actualXml.indexOf("<stackTraceAsString>"))
                + actualXml.substring(actualXml.indexOf("</stackTraceAsString>") + 22);
            actualXml = actualXml.replace("   </ns2:ServiceException>", "</ns2:ServiceException>");
        }
        Assertions.assertEquals(expectedXml, actualXml,
                                "Expected web service response to be equal");
    }

    private String responseAsXml() {
        return responseAsXml(response);
    }

    @SneakyThrows
    private String responseAsXml(JAXBElement<O> response) {
        JAXBContext jaxbContext = JAXBContext.newInstance(ServiceException.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        StringWriter stringWriter = new StringWriter();
        jaxbMarshaller.marshal(response, stringWriter);
        return AbstractSoapTestClient.toXmlString(new StringSource(stringWriter.toString()));
    }

    @SneakyThrows
    private JAXBElement<O> toXml(String string, Class<O> clazz) {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        jakarta.xml.bind.Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return jaxbUnmarshaller.unmarshal(new StringSource(string), clazz);
    }
}

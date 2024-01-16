package uk.gov.hmcts.darts.event.controller;

import com.viqsoultions.DARNotifyEvent;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.hmcts.darts.cache.token.config.CachePropertiesImpl;
import uk.gov.hmcts.darts.config.CacheConfig;
import uk.gov.hmcts.darts.config.OauthTokenGenerator;
import uk.gov.hmcts.darts.event.client.DarNotifyEventClient;
import uk.gov.hmcts.darts.event.config.DarNotifyEventConfiguration;
import uk.gov.hmcts.darts.event.service.impl.DarNotifyEventServiceImpl;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.stream.StreamSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DarNotifyController.class)
@Import({DarNotifyEventConfiguration.class, DarNotifyEventClient.class, DarNotifyEventServiceImpl.class, CachePropertiesImpl.class, CacheConfig.class, OauthTokenGenerator.class, RestTemplate.class})
class DarNotifyControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    private Jaxb2Marshaller jaxb2Marshaller;

    @MockBean
    private WebServiceTemplate mockWebServiceTemplate;

    // Initial test added here to check the DARNotifyEvent XML request for convenience
    @Test
    void shouldMarshalDarNotifyEventAsExpected() throws JAXBException {
        Unmarshaller unmarshaller = jaxb2Marshaller.createUnmarshaller();
        String expectedDarNotifyEventXml = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <ns2:DARNotifyEvent xmlns:ns2="http://www.VIQSoultions.com">
                    <XMLEventDocument>
                        <event type="3" Y="2023" M="6" D="19" H="15" MIN="30" S="25">
                            <courthouse>courthouse</courthouse>
                            <courtroom>courtroom</courtroom>
                            <case_numbers>
                                <case_number>A123456</case_number>
                            </case_numbers>
                        </event>
                    </XMLEventDocument>
                </ns2:DARNotifyEvent>
                """;
        Object jaxbElement = unmarshaller.unmarshal(new StreamSource(new StringReader(expectedDarNotifyEventXml)));
        assertThat(jaxbElement instanceof DARNotifyEvent);

        Marshaller marshaller = jaxb2Marshaller.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        marshaller.marshal((DARNotifyEvent) jaxbElement, sw);
        assertThat(sw.toString()).isEqualTo(expectedDarNotifyEventXml);
    }

    // This SOAP Web Service operation (DARNotifyEvent) still needs to be fully integration tested
    @Test
    void shouldSendDarNotifyEventSoapAction() throws Exception {
        String requestBody = """
                {
                  "notification_type": "3",
                  "timestamp": "2023-06-19T14:52:40.637Z",
                  "courthouse": "Test Court",
                  "courtroom": "1",
                  "case_numbers": [
                    "A123456"
                  ]
                }
                """;
        MockHttpServletRequestBuilder requestBuilder = post("/events/dar-notify")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody);

        MvcResult response = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful()).andReturn();

        assertThat(response.getResponse().getContentAsString()).isEqualTo("");
        Mockito.verify(mockWebServiceTemplate).marshalSendAndReceive(
                eq("http://localhost:4551/VIQDARNotifyEvent/DARNotifyEvent.asmx"),
                any(DARNotifyEvent.class),
                any(SoapActionCallback.class)
        );
        Mockito.verifyNoMoreInteractions(mockWebServiceTemplate);
    }

}

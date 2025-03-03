package uk.gov.hmcts.darts.testutils.stub;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToXml;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

@Component
public class DarPcStub {

    private static final String BASE_API_URL = "/VIQDARNotifyEvent/DARNotifyEvent.asmx";
    private static final String DAR_PC_SUCCESS_RESPONSE = """
        <?xml version="1.0" encoding="utf-8"?><soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema"><soap:Body><DARNotifyEventResponse xmlns="http://www.VIQSoultions.com"><DARNotifyEventResult>0</DARNotifyEventResult></DARNotifyEventResponse></soap:Body></soap:Envelope>""";

    private static final String DAR_PC_MALFORMED_RESPONSE = """
        <?xml version="1.0" encoding="utf-8"?><soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema"><soap:Body><DARNotifyEventResponse xmlns="http://www.VIQSoultions.com"><DARNotifyEventResult>1</DARNotifyEventResult></DARNotifyEventResponse></soap:Body></soap:Envelope>""";


    public void respondWithSuccessResponse(OffsetDateTime responseDateTime) {
        stubFor(post(urlEqualTo(BASE_API_URL)).willReturn(
            aResponse().withHeader("Content-Type", "text/xml")
                .withHeader("Date", responseDateTime.format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .withBody(DAR_PC_SUCCESS_RESPONSE)));
    }

    public void respondWithMalformedResponse() {
        stubFor(post(urlEqualTo(BASE_API_URL)).willReturn(
            aResponse().withHeader("Content-Type", "text/xml")
                .withBody(DAR_PC_MALFORMED_RESPONSE)));
    }

    public void verifyNotificationReceivedWithBody(String body) {
        verify(exactly(1), postRequestedFor(urlEqualTo(BASE_API_URL))
            .withRequestBody(equalToXml(body, true)));
    }

    public void verifyNoNotificationReceived() {
        verify(exactly(0), postRequestedFor(urlEqualTo(BASE_API_URL)));
    }

    public void reset() {
        WireMock.reset();
    }
}

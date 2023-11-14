package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.AddAudioResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.TestUtils;
import uk.gov.hmcts.darts.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.utils.client.darts.DartsClientProvider;
import uk.gov.hmcts.darts.utils.client.darts.DartsGatewayClient;

@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class AddAudioWebServiceTest  extends IntegrationBase {

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void addAudio(DartsGatewayClient client) throws Exception {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/addAudio/soapRequest.xml");


        String expectedResponseStr = TestUtils.getContentsFromFile(
            "payloads/addAudio/expectedResponse.xml");

        SoapAssertionUtil<AddAudioResponse> response = client.addAudio(getGatewayUri(), soapRequestStr);
        response.assertIdenticalResponse(client.convertData(expectedResponseStr, AddAudioResponse.class).getValue());
    }

}

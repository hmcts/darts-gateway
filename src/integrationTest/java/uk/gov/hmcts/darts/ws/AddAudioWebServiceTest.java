package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.AddAudioResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.TestUtils;
import uk.gov.hmcts.darts.utils.client.ClientProvider;
import uk.gov.hmcts.darts.utils.client.DartsGatewayAssertionUtil;
import uk.gov.hmcts.darts.utils.client.DartsGatewayClient;

@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class AddAudioWebServiceTest  extends IntegrationBase {

    @ParameterizedTest
    @ArgumentsSource(ClientProvider.class)
    void addAudio(DartsGatewayClient client) throws Exception {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/addAudio/soapRequest.xml");


        String expectedResponseStr = TestUtils.getContentsFromFile(
            "payloads/addAudio/expectedResponse.xml");

        DartsGatewayAssertionUtil<AddAudioResponse> response = client.addAudio(getGatewayUri(), soapRequestStr);
        response.assertIdenticalResponse(client.convertData(expectedResponseStr, AddAudioResponse.class).getValue());
    }

}

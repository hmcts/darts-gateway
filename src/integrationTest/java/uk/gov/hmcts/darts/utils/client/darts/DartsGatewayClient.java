package uk.gov.hmcts.darts.utils.client.darts;

import com.service.mojdarts.synapps.com.AddAudioResponse;
import com.service.mojdarts.synapps.com.AddCaseResponse;
import com.service.mojdarts.synapps.com.AddDocumentResponse;
import com.service.mojdarts.synapps.com.AddLogEntryResponse;
import com.service.mojdarts.synapps.com.GetCasesResponse;
import com.service.mojdarts.synapps.com.GetCourtLogResponse;
import com.service.mojdarts.synapps.com.RegisterNodeResponse;
import jakarta.xml.bind.JAXBException;
import uk.gov.hmcts.darts.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.utils.client.SoapTestClient;

import java.net.MalformedURLException;
import java.net.URL;

@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public interface DartsGatewayClient extends SoapTestClient {

    SoapAssertionUtil<GetCasesResponse> getCases(URL uri, String payload) throws MalformedURLException, JAXBException;

    SoapAssertionUtil<AddDocumentResponse> addDocument(URL uri, String payload) throws JAXBException;

    SoapAssertionUtil<GetCourtLogResponse> getCourtLogs(URL uri, String payload) throws JAXBException;

    SoapAssertionUtil<AddLogEntryResponse> postCourtLogs(URL uri, String payload) throws JAXBException;

    SoapAssertionUtil<AddCaseResponse> addCases(URL uri, String payload) throws MalformedURLException, JAXBException;

    SoapAssertionUtil<RegisterNodeResponse> registerNode(URL uri, String payload) throws JAXBException;

    SoapAssertionUtil<AddAudioResponse> addAudio(URL uri, String payload) throws JAXBException;
}

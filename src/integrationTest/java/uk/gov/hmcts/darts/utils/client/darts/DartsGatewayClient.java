package uk.gov.hmcts.darts.utils.client.darts;

import com.service.mojdarts.synapps.com.*;
import jakarta.xml.bind.JAXBElement;
import uk.gov.hmcts.darts.utils.client.SOAPAssertionUtil;
import uk.gov.hmcts.darts.utils.client.SOAPTestClient;

import java.lang.Exception;
import java.net.URL;
import java.util.function.Function;

@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public interface DartsGatewayClient extends SOAPTestClient {

    SOAPAssertionUtil<GetCasesResponse> getCases(URL uri, String payload) throws Exception;

    SOAPAssertionUtil<AddDocumentResponse> addDocument(URL uri, String payload) throws Exception;

    SOAPAssertionUtil<GetCourtLogResponse> getCourtLogs(URL uri, String payload) throws Exception;

    SOAPAssertionUtil<AddLogEntryResponse> postCourtLogs(URL uri, String payload) throws Exception;

    SOAPAssertionUtil<AddCaseResponse> addCases(URL uri, String payload) throws Exception;

    SOAPAssertionUtil<RegisterNodeResponse> registerNode(URL uri, String payload) throws Exception;

    SOAPAssertionUtil<AddAudioResponse> addAudio(URL uri, String payload) throws Exception;
}

package uk.gov.hmcts.darts.utils.client.darts;

import com.service.mojdarts.synapps.com.AddCaseResponse;
import com.service.mojdarts.synapps.com.AddDocumentResponse;
import com.service.mojdarts.synapps.com.AddLogEntryResponse;
import com.service.mojdarts.synapps.com.GetCasesResponse;
import com.service.mojdarts.synapps.com.GetCourtLogResponse;
import com.service.mojdarts.synapps.com.RegisterNodeResponse;
import jakarta.xml.bind.JAXBElement;
import uk.gov.hmcts.darts.utils.client.SOAPAssertionUtil;
import uk.gov.hmcts.darts.utils.client.SOAPTestClient;

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
}

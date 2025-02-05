package uk.gov.hmcts.darts.common.utils.client.darts;

import com.emc.documentum.fs.rt.ServiceException;
import com.service.mojdarts.synapps.com.AddAudioResponse;
import com.service.mojdarts.synapps.com.AddCaseResponse;
import com.service.mojdarts.synapps.com.AddDocumentResponse;
import com.service.mojdarts.synapps.com.AddLogEntryResponse;
import com.service.mojdarts.synapps.com.GetCasesResponse;
import com.service.mojdarts.synapps.com.GetCourtLogResponse;
import com.service.mojdarts.synapps.com.RegisterNodeResponse;
import jakarta.xml.bind.JAXBException;
import uk.gov.hmcts.darts.common.utils.client.SoapAssertionUtil;

import java.net.MalformedURLException;
import java.net.URL;

@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public interface DartsGatewayClient extends uk.gov.hmcts.darts.common.utils.client.SoapTestClient {

    SoapAssertionUtil<GetCasesResponse> getCases(URL uri, String payload) throws MalformedURLException, JAXBException;

    SoapAssertionUtil<AddDocumentResponse> addDocument(URL uri, String payload);

    SoapAssertionUtil<ServiceException> addDocumentException(URL uri, String payload);

    SoapAssertionUtil<GetCourtLogResponse> getCourtLogs(URL uri, String payload);

    SoapAssertionUtil<AddLogEntryResponse> postCourtLogs(URL uri, String payload);

    SoapAssertionUtil<ServiceException> postCourtLogsException(URL uri, String payload);

    SoapAssertionUtil<AddCaseResponse> addCases(URL uri, String payload) throws MalformedURLException, JAXBException;

    SoapAssertionUtil<RegisterNodeResponse> registerNode(URL uri, String payload);

    SoapAssertionUtil<AddAudioResponse> addAudio(URL uri, String payload);

    SoapAssertionUtil<ServiceException> addAudioException(URL uri, String payload);

    SoapAssertionUtil<ServiceException> getCasesException(URL uri, String payload);
}

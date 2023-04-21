package uk.gov.hmcts.darts.apim.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.bind.*;

import com.service.mojdarts.synapps.com.AddLogEntry;
import com.service.mojdarts.synapps.com.AddLogEntryResponse;

@Endpoint
public class CaseEndpoint {

    private static final String NAMESPACE_URI = "http://com.synapps.mojdarts.service.com";

	@Autowired
	public CaseEndpoint() {
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "addLogEntry")
	@ResponsePayload
	public AddLogEntryResponse addLogEntry(@RequestPayload JAXBElement<AddLogEntry> request) {
		AddLogEntryResponse response = new AddLogEntryResponse();
        return response;
	}

}

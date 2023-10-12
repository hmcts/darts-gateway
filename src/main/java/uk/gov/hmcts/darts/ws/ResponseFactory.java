package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.AddCaseResponse;
import com.service.mojdarts.synapps.com.AddDocumentResponse;
import com.service.mojdarts.synapps.com.AddLogEntryResponse;
import com.service.mojdarts.synapps.com.GetCasesResponse;
import com.service.mojdarts.synapps.com.GetCourtLogResponse;
import com.service.mojdarts.synapps.com.ObjectFactory;
import com.service.mojdarts.synapps.com.RegisterNodeResponse;
import com.synapps.moj.dfs.response.DARTSResponse;

public final class ResponseFactory {

    private ResponseFactory() {
    }

    public static AddDocumentResponse getAddDocumentResponse() {
        AddDocumentResponse documentResponse = new ObjectFactory().createAddDocumentResponse();
        documentResponse.setReturn(new DARTSResponse());
        return documentResponse;
    }


    public static GetCasesResponse getCasesResponse() {
        GetCasesResponse casesResponse = new ObjectFactory().createGetCasesResponse();
        casesResponse.setReturn(new com.synapps.moj.dfs.response.GetCasesResponse());
        return casesResponse;
    }


    public static AddCaseResponse getAddCaseResponse() {
        AddCaseResponse addCaseResponse = new ObjectFactory().createAddCaseResponse();
        addCaseResponse.setReturn(new com.synapps.moj.dfs.response.GetCasesResponse());
        return addCaseResponse;
    }

    public static GetCourtLogResponse getCourtLogResponse() {
        GetCourtLogResponse addCaseResponseLog = new ObjectFactory().createGetCourtLogResponse();
        addCaseResponseLog.setReturn(new com.synapps.moj.dfs.response.GetCourtLogResponse());
        return addCaseResponseLog;
    }


    public static AddLogEntryResponse getAddLogEntryResponse() {
        AddLogEntryResponse addLogEntryResponse = new ObjectFactory().createAddLogEntryResponse();
        addLogEntryResponse.setReturn(new DARTSResponse());

        return addLogEntryResponse;
    }

    public static RegisterNodeResponse getRegisterNodeResponse() {
        RegisterNodeResponse registerNodeResponse = new ObjectFactory().createRegisterNodeResponse();
        registerNodeResponse.setReturn(new com.synapps.moj.dfs.response.RegisterNodeResponse());
        return registerNodeResponse;
    }
}

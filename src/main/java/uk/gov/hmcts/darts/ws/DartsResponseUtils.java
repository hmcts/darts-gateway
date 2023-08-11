package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.AddCaseResponse;
import com.synapps.moj.dfs.response.DARTSResponse;
import com.synapps.moj.dfs.response.GetCourtLogResponse;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;

import static uk.gov.hmcts.darts.ws.CodeAndMessage.ERROR;
import static uk.gov.hmcts.darts.ws.CodeAndMessage.INVALID_XML;
import static uk.gov.hmcts.darts.ws.CodeAndMessage.OK;

@Component
public class DartsResponseUtils {

    DARTSResponse createDartsResponseMessage(Exception ex) {
        if (ex instanceof DartsException) {
            return createDartsResponseMessage(((DartsException) ex).getCodeAndMessage());
        }

        if (ex instanceof DartsValidationException) {
            return createDartsResponseMessage(INVALID_XML);
        }

        return createDartsResponseMessage(ERROR);
    }

    public DARTSResponse createDartsResponseMessage(CodeAndMessage codeAndMessage) {
        var responseMessage = new DARTSResponse();
        responseMessage.setCode(codeAndMessage.getCode());
        responseMessage.setMessage(codeAndMessage.getMessage());

        return responseMessage;
    }

    GetCourtLogResponse createCourtLogResponse(CodeAndMessage codeAndMessage) {
        var responseMessage = new GetCourtLogResponse();
        responseMessage.setCode(codeAndMessage.getCode());
        responseMessage.setMessage(codeAndMessage.getMessage());

        return responseMessage;
    }

    GetCourtLogResponse createCourtLogResponse(Exception ex) {
        if (ex instanceof DartsException) {
            return createCourtLogResponse(((DartsException) ex).getCodeAndMessage());
        }

        if (ex instanceof DartsValidationException) {
            return createCourtLogResponse(INVALID_XML);
        }

        return createCourtLogResponse(ERROR);
    }

    public AddCaseResponse createErrorAddCaseResponse(Exception exception) {
        DARTSResponse value = createDartsResponseMessage(ERROR);
        value.setMessage(exception.getMessage());

        AddCaseResponse response = new AddCaseResponse();
        response.setReturn(value);
        return response;
    }

    public AddCaseResponse createSuccessfulAddCaseResponse() {
        AddCaseResponse response = new AddCaseResponse();
        response.setReturn(createDartsResponseMessage(OK));
        return response;
    }
}

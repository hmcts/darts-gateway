package uk.gov.hmcts.darts.ws;

import com.synapps.moj.dfs.response.DARTSResponse;
import com.synapps.moj.dfs.response.GetCourtLogResponse;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;

import static uk.gov.hmcts.darts.ws.CodeAndMessage.ERROR;
import static uk.gov.hmcts.darts.ws.CodeAndMessage.INVALID_XML;

@Component
public class DartsResponseUtils {

    DARTSResponse createResponseMessage(Exception ex) {
        if (ex instanceof DartsException) {
            return createResponseMessage(((DartsException) ex).getCodeAndMessage());
        }

        if (ex instanceof DartsValidationException) {
            return createResponseMessage(INVALID_XML);
        }

        return createResponseMessage(ERROR);
    }

    DARTSResponse createResponseMessage(CodeAndMessage codeAndMessage) {
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
}

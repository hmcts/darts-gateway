package uk.gov.hmcts.darts.soap;

import com.synapps.moj.dfs.response.DARTSResponse;
import org.springframework.stereotype.Component;

import static uk.gov.hmcts.darts.soap.ErrorCodeAndMessage.ERROR;

@Component
public class DartsResponseUtils {

    DARTSResponse createResponseMessage(Exception ex) {
        if (ex instanceof DartsException) {
            return createResponseMessage(((DartsException) ex).getCodeAndMessage());
        }

        return createResponseMessage(ERROR);
    }

    DARTSResponse createResponseMessage(ErrorCodeAndMessage codeAndMessage) {
        DARTSResponse responseMessage = new DARTSResponse();
        responseMessage.setCode(codeAndMessage.getCode());
        responseMessage.setMessage(codeAndMessage.getMessage());

        return responseMessage;
    }

}

package uk.gov.hmcts.darts.soap;

import com.synapps.moj.dfs.response.DARTSResponse;
import org.springframework.stereotype.Component;

import static uk.gov.hmcts.darts.soap.DartsResponseCodeAndMessage.ERROR;

@Component
public class DartsResponseUtils {

    DARTSResponse responseMessageFromException(Exception ex) {
        if (ex instanceof DartsException) {
            return responseMessageFromCodeAndMessage(((DartsException) ex).getCodeAndMessage());
        }

        return responseMessageFromCodeAndMessage(ERROR);
    }

    DARTSResponse responseMessageFromCodeAndMessage(DartsResponseCodeAndMessage codeAndMessage) {
        DARTSResponse responseMessage = new DARTSResponse();
        responseMessage.setCode(codeAndMessage.getCode());
        responseMessage.setMessage(codeAndMessage.getMessage());

        return responseMessage;
    }

}

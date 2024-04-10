package uk.gov.hmcts.darts.utilities;

import com.synapps.moj.dfs.response.DARTSResponse;
import lombok.experimental.UtilityClass;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

@UtilityClass
public class DartsResponseUtil {

    public void addMessageAndCode(DARTSResponse response, CodeAndMessage codeAndMessage) {
        response.setCode(codeAndMessage.getCode());
        response.setMessage(codeAndMessage.getMessage());
    }
}

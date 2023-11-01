package uk.gov.hmcts.darts.addaudio;

import com.service.mojdarts.synapps.com.AddAudio;
import com.synapps.moj.dfs.response.DARTSResponse;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

@Service
public class AddAudioRoute {

    public DARTSResponse route(AddAudio value) {
        // TODO add business logic
        CodeAndMessage okResponse = CodeAndMessage.OK;
        return okResponse.getResponse();
    }
}

package uk.gov.hmcts.darts.addaudio;

import com.service.mojdarts.synapps.com.AddAudio;
import com.synapps.moj.dfs.response.DARTSResponse;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequest;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.io.IOException;
import java.util.Optional;

@Service
public class AddAudioRoute {

    public DARTSResponse route(AddAudio value) {
        Optional<XmlWithFileMultiPartRequest> request = XmlWithFileMultiPartRequest.getCurrentRequest();
        if (request.isPresent()) {
            try {
                request.get().consumeFileBinary(file -> {
                });
            } catch (IOException e) {
                CodeAndMessage okResponse = CodeAndMessage.ERROR;
                return okResponse.getResponse();
            }
        }

        // TODO add business logic
        CodeAndMessage okResponse = CodeAndMessage.OK;
        return okResponse.getResponse();
    }
}

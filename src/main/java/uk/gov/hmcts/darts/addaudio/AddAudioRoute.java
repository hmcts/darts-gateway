package uk.gov.hmcts.darts.addaudio;

import com.service.mojdarts.synapps.com.AddAudio;
import com.synapps.moj.dfs.response.DARTSResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.ws.CodeAndMessage;
import uk.gov.hmcts.darts.ws.multipart.MTOMMetaDataAndUploadRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class AddAudioRoute {

    public DARTSResponse route(AddAudio value) {
        Optional<MTOMMetaDataAndUploadRequest> request = MTOMMetaDataAndUploadRequest.getCurrentRequest();
        if (request.isPresent()) {
            try {
                request.get().consumeFileBinary(file -> {
                });
            }
            catch (IOException e) {
                CodeAndMessage okResponse = CodeAndMessage.ERROR;
                return okResponse.getResponse();
            }
        }

        // TODO add business logic
        CodeAndMessage okResponse = CodeAndMessage.OK;
        return okResponse.getResponse();
    }
}

package uk.gov.hmcts.darts.addaudio;

import com.service.mojdarts.synapps.com.addaudio.Audio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.common.util.DateConverters;
import uk.gov.hmcts.darts.model.audio.AddAudioMetadataRequest;



@RequiredArgsConstructor
@Service
public class AddAudioMapper {
    private final DateConverters dateConverters;

    public AddAudioMetadataRequest mapToDartsApi(Audio legacyAudio) {
        AddAudioMetadataRequest addAudioMetadataRequest = new AddAudioMetadataRequest();
        addAudioMetadataRequest.setStartedAt(dateConverters.offsetDateTimeFrom(legacyAudio.getStart()));
        addAudioMetadataRequest.setEndedAt(dateConverters.offsetDateTimeFrom(legacyAudio.getEnd()));
        addAudioMetadataRequest.setChannel(legacyAudio.getChannel().intValue());
        addAudioMetadataRequest.setTotalChannels(legacyAudio.getMaxChannels().intValue());
        addAudioMetadataRequest.setFormat(legacyAudio.getMediaformat());
        addAudioMetadataRequest.setMediaFile(legacyAudio.getMediafile());
        addAudioMetadataRequest.setFilename(legacyAudio.getMediafile());
        addAudioMetadataRequest.setCourthouse(legacyAudio.getCourthouse());
        addAudioMetadataRequest.setCourtroom(legacyAudio.getCourtroom());
        addAudioMetadataRequest.setCases(legacyAudio.getCaseNumbers().getCaseNumber());

        return addAudioMetadataRequest;
    }

}

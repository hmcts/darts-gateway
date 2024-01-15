package uk.gov.hmcts.darts.common.client.mapper;


import uk.gov.hmcts.darts.common.client.exeption.addaudio.AudioAPIAddAudioException;
import uk.gov.hmcts.darts.model.audio.AddAudioErrorCode;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

@SuppressWarnings("unchecked")
public class AudioAPIProblemResponseMapper extends AbstractAPIProblemResponseMapper {
    {
        var opmapping = new ProblemResponseMappingOperation
            .ProblemResponseMappingOperationBuilder<AddAudioErrorCode>()
            .operation(AddAudioErrorCode.class)
            .exception(this::createAddAudioException).build();

        opmapping.addMapping(opmapping.createProblemResponseMapping()
                                 .problem(AddAudioErrorCode.ADD_AUDIO_COURT_HOUSE_NOT_FOUND)
                                 .message(CodeAndMessage.NOT_FOUND_COURTHOUSE).build());
        addOperationMappings(opmapping);
    }

    private AudioAPIAddAudioException createAddAudioException(ProblemAndMapping problemAndMapping) {
        return new AudioAPIAddAudioException(
            (ProblemResponseMapping<AddAudioErrorCode>) problemAndMapping.getMapping(), problemAndMapping.getProblem());
    }

}

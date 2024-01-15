package uk.gov.hmcts.darts.common.client.exeption.addaudio;

import lombok.Getter;
import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.common.client.mapper.ProblemResponseMapping;
import uk.gov.hmcts.darts.model.audio.AddAudioErrorCode;
import uk.gov.hmcts.darts.model.audio.Problem;

@Getter
@SuppressWarnings("java:S110")
public class AudioAPIAddAudioException extends ClientProblemException {
    private final transient ProblemResponseMapping<AddAudioErrorCode> mapping;

    public AudioAPIAddAudioException(Throwable cause, ProblemResponseMapping<AddAudioErrorCode> mapping, Problem problem) {
        super(cause, mapping.getMessage(), problem);
        this.mapping = mapping;
    }

    public AudioAPIAddAudioException(ProblemResponseMapping<AddAudioErrorCode> addCases, Problem problem) {
        this(null, addCases, problem);
    }
}

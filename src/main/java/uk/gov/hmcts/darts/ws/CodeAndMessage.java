package uk.gov.hmcts.darts.ws;

import com.synapps.moj.dfs.response.DARTSResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CodeAndMessage {

    ERROR("500", null, false),
    INVALID_XML("400", "Invalid XML Document", true),
    SYSTEM_TYPE_NOT_FOUND("400", "System type not found", true),
    NOT_FOUND_COURTHOUSE("404", "Courthouse Not Found", true),
    NOT_FOUND_HANLDER("404", "Handler Not Found", true),
    AUDIO_TOO_LARGE("404", "Audio Too Large", true),
    OK("200", "OK", true);

    private final String code;
    private final String message;
    private final boolean isSuccessResponse;

    public DARTSResponse getResponse() {
        return getResponse(code, message);
    }

    public static DARTSResponse getResponse(String code, String message) {
        DARTSResponse response = new DARTSResponse();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    public static DARTSResponse getResponse(CodeAndMessage codeAndMessage) {
        DARTSResponse response = new DARTSResponse();
        response.setCode(codeAndMessage.getCode());
        response.setMessage(codeAndMessage.getMessage());
        return response;
    }
}

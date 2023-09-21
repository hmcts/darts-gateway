package uk.gov.hmcts.darts.common.client.exeption;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.darts.common.client.mapper.APIProblemResponseMapper;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.ws.CodeAndMessage;
import uk.gov.hmcts.darts.ws.DartsException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractClientProblemDecoder implements ErrorDecoder {

    private final List<APIProblemResponseMapper> responseMappers;

    @Override
    public Exception decode(String methodKey, Response response) {
        Exception returnEx;
        try {
            Problem problem = getProblem(response);
            returnEx = getExceptionForProblem(problem);
        } catch (IOException ioEx) {
            log.error("Failed to read the problem json", ioEx);
            returnEx = new DartsException(ioEx, CodeAndMessage.ERROR);
        }

        return returnEx;
    }

    private ClientProblemException getExceptionForProblem(Problem problem) {
        ClientProblemException returnEx = null;
        Optional<APIProblemResponseMapper> identifiedProblem
            = responseMappers.stream().filter(e -> e.getExceptionForProblem(problem).isPresent()).findFirst();

        if (identifiedProblem.isPresent()) {
            Optional<ClientProblemException> exception = identifiedProblem.get().getExceptionForProblem(problem);

            if (exception.isPresent()) {
                returnEx = exception.get();
            }
        } else {
            returnEx = new ClientProblemException(problem);
        }

        return returnEx;
    }

    protected abstract Problem getProblem(Response response) throws IOException;
}

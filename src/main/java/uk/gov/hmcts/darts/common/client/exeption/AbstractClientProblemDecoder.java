package uk.gov.hmcts.darts.common.client.exeption;

import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.web.client.HttpStatusCodeException;
import uk.gov.hmcts.darts.common.client.mapper.APIProblemResponseMapper;
import uk.gov.hmcts.darts.common.exceptions.DartsException;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractClientProblemDecoder {

    private final List<APIProblemResponseMapper> responseMappers;
    public static final String RESPONSE_PREFIX = "Problem response from upstream : ";

    public Exception decode(String methodKey, Response response) {
        Exception returnEx;
        try {
            String payload = IOUtils.toString(response.body().asInputStream(), Charset.defaultCharset());
            log.trace(RESPONSE_PREFIX.concat("{}"), response.status() + " Server Error:" + payload);
            Problem problem = getProblem(IOUtils.toInputStream(payload, Charset.defaultCharset()));
            returnEx = getExceptionForProblem(problem);
        } catch (Exception ioEx) {
            log.error("Failed to read the problem json", ioEx);
            returnEx = new DartsException(ioEx, CodeAndMessage.ERROR);
        }

        return returnEx;
    }

    public DartsException decode(HttpStatusCodeException response) {
        log.trace(RESPONSE_PREFIX.concat("{}"), response.getMessage());
        try (ByteArrayInputStream is = new ByteArrayInputStream(response.getResponseBodyAsByteArray())) {
            Problem problem = getProblem(is);
            return getExceptionForProblem(problem);
        } catch (Exception ioEx) {
            log.error("Failed to read the problem json so");
            return new DartsException(ioEx, CodeAndMessage.ERROR);
        }
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


    protected abstract Problem getProblem(InputStream response) throws IOException;
}
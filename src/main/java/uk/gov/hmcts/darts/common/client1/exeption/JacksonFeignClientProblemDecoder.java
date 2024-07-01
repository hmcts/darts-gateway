package uk.gov.hmcts.darts.common.client1.exeption;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.darts.common.client1.mapper.APIProblemResponseMapper;
import uk.gov.hmcts.darts.config.ServiceConfig;
import uk.gov.hmcts.darts.model.audio.Problem;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
public class JacksonFeignClientProblemDecoder extends AbstractClientProblemDecoder implements ErrorDecoder {

    public JacksonFeignClientProblemDecoder(List<APIProblemResponseMapper> responseMappers) {
        super(responseMappers);
    }

    @Override
    protected Problem getProblem(InputStream response) throws IOException {
        ObjectMapper mapper = ServiceConfig.getServiceObjectMapper();
        Problem problem = mapper.readValue(response, Problem.class);
        log.error("A problem occurred when communicating downstream {}", problem.toString());
        return problem;
    }
}

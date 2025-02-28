package uk.gov.hmcts.darts.common.client.exeption;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.darts.common.client.mapper.APIProblemResponseMapper;
import uk.gov.hmcts.darts.config.ServiceConfig;
import uk.gov.hmcts.darts.model.audio.Problem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JacksonFeignClientProblemDecoder extends AbstractClientProblemDecoder implements ErrorDecoder {

    public JacksonFeignClientProblemDecoder(List<APIProblemResponseMapper> responseMappers) {
        super(responseMappers);
    }

    @Override
    protected Problem getProblem(InputStream response) throws IOException {
        log.error("A problem occurred when communicating downstream {}", toString(response));
        ObjectMapper mapper = ServiceConfig.getServiceObjectMapper();
        return mapper.readValue(response, Problem.class);
    }

    private String toString(InputStream inputStream) {
        return new BufferedReader(
            new InputStreamReader(inputStream, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
    }
}

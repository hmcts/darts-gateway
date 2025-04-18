package uk.gov.hmcts.darts.common.client.exeption;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
        String responseStr = toString(response);
        try {
            log.error("A problem occurred when communicating downstream {}", responseStr);
            ObjectMapper mapper = ServiceConfig.getServiceObjectMapper();
            return mapper.readValue(responseStr, Problem.class);
        } catch (Exception e) {
            log.error("Failed to read the problem", e);
            return new Problem()
                .title("An unknown error occurred")
                .detail(responseStr)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private String toString(InputStream inputStream) {
        return new BufferedReader(
            new InputStreamReader(inputStream, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
    }
}

package uk.gov.hmcts.darts.common.client.exeption;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.hmcts.darts.common.client.mapper.APIProblemResponseMapper;
import uk.gov.hmcts.darts.config.ServiceConfig;
import uk.gov.hmcts.darts.model.audio.Problem;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class JacksonDartsClientProblemDecoder extends AbstractClientProblemDecoder implements DartsClientProblemDecoder {

    public JacksonDartsClientProblemDecoder(List<APIProblemResponseMapper> responseMappers) {
        super(responseMappers);
    }

    @Override
    protected Problem getProblem(InputStream response) throws IOException {
        ObjectMapper mapper = ServiceConfig.getServiceObjectMapper();
        return mapper.readValue(response, Problem.class);
    }
}
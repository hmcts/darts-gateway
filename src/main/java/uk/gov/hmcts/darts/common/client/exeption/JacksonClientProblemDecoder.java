package uk.gov.hmcts.darts.common.client.exeption;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import uk.gov.hmcts.darts.common.client.mapper.APIProblemResponseMapper;
import uk.gov.hmcts.darts.config.ServiceConfig;
import uk.gov.hmcts.darts.model.audio.Problem;

import java.io.IOException;
import java.util.List;

public class JacksonClientProblemDecoder extends AbstractClientProblemDecoder {

    public JacksonClientProblemDecoder(List<APIProblemResponseMapper> responseMappers) {
        super(responseMappers);
    }

    @Override
    protected Problem getProblem(Response response) throws IOException {
        ObjectMapper mapper = ServiceConfig.getServiceObjectMapper();
        return  mapper.readValue(response.body().asInputStream(), Problem.class);
    }
}

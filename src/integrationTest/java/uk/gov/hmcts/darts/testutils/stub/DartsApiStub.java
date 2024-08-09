package uk.gov.hmcts.darts.testutils.stub;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.zalando.problem.jackson.ProblemModule;

import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

public class DartsApiStub {

    protected ObjectMapper objectMapper;
    private final String baseApiUrl;

    public DartsApiStub(String baseApiUrl) {
        this.baseApiUrl = baseApiUrl;
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new ProblemModule());

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void verifyDoesntReceiveRequest() {
        verify(exactly(0), getRequestedFor(urlEqualTo(baseApiUrl)));
    }
}

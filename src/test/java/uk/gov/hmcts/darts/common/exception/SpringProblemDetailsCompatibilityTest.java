package uk.gov.hmcts.darts.common.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.common.client.exeption.JacksonDartsClientProblemDecoder;
import uk.gov.hmcts.darts.common.client.exeption.JacksonFeignClientProblemDecoder;
import uk.gov.hmcts.darts.config.ServiceConfig;
import uk.gov.hmcts.darts.utilities.TestUtils;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SpringProblemDetailsCompatibilityTest {

    private final ServiceConfig serviceConfig = new ServiceConfig();
    private final ObjectMapper objectMapper = ServiceConfig.getServiceObjectMapper();

    @ParameterizedTest
    @MethodSource("problemResponses")
    void givenSpringProblemDetails_whenFeignDecodes_thenPreservesSoapMapping(String fixture, CodeAndMessage expectedCode) throws IOException {
        String payload = TestUtils.getContentsFromFile("problem-details/" + fixture);
        JsonNode expected = objectMapper.readTree(payload);
        try (Response response = Response.builder()
            .request(Request.create(Request.HttpMethod.POST, "http://darts-api/test", Map.of(), null, StandardCharsets.UTF_8, null))
            .status(expected.get("status").asInt())
            .headers(Map.of(HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_PROBLEM_JSON_VALUE)))
            .body(payload, StandardCharsets.UTF_8)
            .build()) {
            Exception exception = new JacksonFeignClientProblemDecoder(serviceConfig.getResponseMappers()).decode("test", response);

            assertProblem(exception, expected, expectedCode);
        }
    }

    @ParameterizedTest
    @MethodSource("problemResponses")
    @SuppressWarnings("PMD.LooseCoupling") // Spring exception factories require HttpHeaders.
    void givenSpringProblemDetails_whenRestTemplateDecodes_thenPreservesSoapMapping(String fixture, CodeAndMessage expectedCode) throws IOException {
        String payload = TestUtils.getContentsFromFile("problem-details/" + fixture);
        JsonNode expected = objectMapper.readTree(payload);
        HttpStatus status = HttpStatus.valueOf(expected.get("status").asInt());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        byte[] body = payload.getBytes(StandardCharsets.UTF_8);
        HttpStatusCodeException response = status.is4xxClientError()
            ? HttpClientErrorException.create(status, status.getReasonPhrase(), headers, body, StandardCharsets.UTF_8)
            : HttpServerErrorException.create(status, status.getReasonPhrase(), headers, body, StandardCharsets.UTF_8);

        Exception exception = new JacksonDartsClientProblemDecoder(serviceConfig.getResponseMappers()).decode(response);

        assertProblem(exception, expected, expectedCode);
    }

    private static Stream<Arguments> problemResponses() {
        return Stream.of(
            Arguments.of("validation.json", CodeAndMessage.INVALID_XML),
            Arguments.of("malformed-json.json", CodeAndMessage.INVALID_XML),
            Arguments.of("courthouse-not-found.json", CodeAndMessage.NOT_FOUND_COURTHOUSE),
            Arguments.of("event-handler-not-found.json", CodeAndMessage.NOT_FOUND_HANLDER),
            Arguments.of("daily-list-failure.json", CodeAndMessage.ERROR),
            Arguments.of("unauthorized.json", CodeAndMessage.ERROR),
            Arguments.of("internal-server-error.json", CodeAndMessage.ERROR)
        );
    }

    private void assertProblem(Exception exception, JsonNode expected, CodeAndMessage expectedCode) {
        assertThat(exception).isInstanceOf(ClientProblemException.class);
        ClientProblemException problemException = (ClientProblemException) exception;
        assertThat(problemException.getCodeAndMessage()).isEqualTo(expectedCode);
        assertThat(problemException.getProblem().getType().toString()).isEqualTo(expected.get("type").asText());
        assertThat(problemException.getProblem().getTitle()).isEqualTo(expected.get("title").asText());
        assertThat(problemException.getProblem().getStatus()).isEqualTo(expected.get("status").asInt());
        assertThat(problemException.getProblem().getDetail()).isEqualTo(expected.path("detail").textValue());
    }
}

package uk.gov.hmcts.darts.common.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.common.client.exeption.JacksonDartsClientProblemDecoder;
import uk.gov.hmcts.darts.common.client.mapper.APIProblemResponseMapper;
import uk.gov.hmcts.darts.common.exceptions.DartsException;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.utilities.TestUtils;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JacksonDartsClientProblemDecoderTest {

    private String dartsApiResponseStr;

    @BeforeEach
    public void before() throws IOException {
        dartsApiResponseStr = TestUtils.getContentsFromFile(
            "tests/client/error/problemResponse.json");
    }

    @Test
    void testDecoderNoProblemMapperFound() {

        APIProblemResponseMapper mapper = Mockito.mock(APIProblemResponseMapper.class);
        Mockito.when(mapper.getExceptionForProblem(Mockito.any(Problem.class))).thenReturn(Optional.empty());

        List<APIProblemResponseMapper> responseMappers = new ArrayList<>();
        responseMappers.add(mapper);

        HttpStatusCodeException ex = new DummyBadRequest("404", new HttpHeaders(), dartsApiResponseStr.getBytes(), Charset.defaultCharset());
        Exception exception = new JacksonDartsClientProblemDecoder(responseMappers).decode(ex);

        Assertions.assertTrue(ClientProblemException.class.isAssignableFrom(exception.getClass()));
        Assertions.assertNotNull(((ClientProblemException) exception).getProblem());
        Assertions.assertEquals(CodeAndMessage.ERROR, ((ClientProblemException) exception).getCodeAndMessage());
    }

    @Test
    void testDecoderErrorsException() {

        ClientProblemException exceptionToReturn = new ClientProblemException(null);
        APIProblemResponseMapper mapper = Mockito.mock(APIProblemResponseMapper.class);
        Mockito.when(mapper.getExceptionForProblem(Mockito.any(Problem.class))).thenReturn(Optional.of(exceptionToReturn));

        List<APIProblemResponseMapper> responseMappers = new ArrayList<>();
        responseMappers.add(mapper);

        HttpStatusCodeException ex = new DummyBadRequest("404", new HttpHeaders(), dartsApiResponseStr.getBytes(), Charset.defaultCharset());
        DartsException exception = new JacksonDartsClientProblemDecoder(responseMappers).decode(ex);
        Assertions.assertSame(CodeAndMessage.ERROR, exception.getCodeAndMessage());
    }

    @Test
    void testDecoderProblemParsingException() throws Exception {
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "tests/client/error/invalidProblemResponse.json");

        ClientProblemException exceptionToReturn = new ClientProblemException(null);
        APIProblemResponseMapper mapper = Mockito.mock(APIProblemResponseMapper.class);
        Mockito.when(mapper.getExceptionForProblem(Mockito.any(Problem.class))).thenReturn(Optional.of(exceptionToReturn));

        List<APIProblemResponseMapper> responseMappers = new ArrayList<>();
        responseMappers.add(mapper);

        HttpStatusCodeException ex = new DummyBadRequest("404", new HttpHeaders(), dartsApiResponseStr.getBytes(), Charset.defaultCharset());
        DartsException exception = new JacksonDartsClientProblemDecoder(responseMappers).decode(ex);
        Assertions.assertEquals(CodeAndMessage.ERROR, exception.getCodeAndMessage());
    }

    @Test
    void getProblem_whenANoneJsonResponseIsReceived_returnInternalServerError() throws IOException {
        String htmlResponseStr = "<html><body><h1>500 Internal Server Error</h1></body></html>";

        Problem problem =  new JacksonDartsClientProblemDecoder(List.of()).getProblem(new ByteArrayInputStream(htmlResponseStr.getBytes()));
        assertThat(problem.getTitle()).isEqualTo("An unknown error occurred");
        assertThat(problem.getDetail()).isEqualTo(htmlResponseStr);
        assertThat(problem.getStatus()).isEqualTo(500);
    }

    static class DummyBadRequest extends HttpClientErrorException {
        @SuppressWarnings("PMD.LooseCoupling")
        public DummyBadRequest(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(HttpStatus.BAD_REQUEST, statusText, headers, body, charset);
        }
    }
}

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
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.utilities.TestUtils;
import uk.gov.hmcts.darts.ws.CodeAndMessage;
import uk.gov.hmcts.darts.ws.DartsException;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class JacksonDartsClientProblemDecoderTest {

    private String dartsApiResponseStr;

    @BeforeEach
    public void before() throws Exception {
        dartsApiResponseStr = TestUtils.getContentsFromFile(
                "tests/client/error/problemResponse.json");
    }

    @Test
    void testDecoderNoProblemMapperFound() throws Exception {

        APIProblemResponseMapper mapper = Mockito.mock(APIProblemResponseMapper.class);
        Mockito.when(mapper.getExceptionForProblem(Mockito.any(Problem.class))).thenReturn(Optional.empty());

        List<APIProblemResponseMapper> responseMappers = new ArrayList<>();
        responseMappers.add(mapper);

        HttpHeaders headers = new HttpHeaders();
        HttpStatusCodeException ex = new DummyBadRequest("404", headers, dartsApiResponseStr.getBytes(), Charset.defaultCharset());
        Exception exception = new JacksonDartsClientProblemDecoder(responseMappers).decode(ex);

        Assertions.assertTrue(ClientProblemException.class.isAssignableFrom(exception.getClass()));
        Assertions.assertNotNull(((ClientProblemException) exception).getProblem());
        Assertions.assertEquals(CodeAndMessage.ERROR, ((ClientProblemException) exception).getCodeAndMessage());
    }

    @Test
    void testDecoderErrorsException() throws Exception {

        ClientProblemException exceptionToReturn = new ClientProblemException(null);
        APIProblemResponseMapper mapper = Mockito.mock(APIProblemResponseMapper.class);
        Mockito.when(mapper.getExceptionForProblem(Mockito.any(Problem.class))).thenReturn(Optional.of(exceptionToReturn));

        List<APIProblemResponseMapper> responseMappers = new ArrayList<>();
        responseMappers.add(mapper);

        HttpHeaders headers = new HttpHeaders();
        HttpStatusCodeException ex = new DummyBadRequest("404", headers, dartsApiResponseStr.getBytes(), Charset.defaultCharset());
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

        HttpHeaders headers = new HttpHeaders();
        HttpStatusCodeException ex = new DummyBadRequest("404", headers, dartsApiResponseStr.getBytes(), Charset.defaultCharset());
        DartsException exception = new JacksonDartsClientProblemDecoder(responseMappers).decode(ex);
        Assertions.assertEquals(CodeAndMessage.ERROR, ((DartsException) exception).getCodeAndMessage());
    }

    class DummyBadRequest extends HttpClientErrorException {
        public DummyBadRequest(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(HttpStatus.BAD_REQUEST, statusText, headers, body, charset);
        }
    }
}

package uk.gov.hmcts.darts.common.exception;

import feign.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.gov.hmcts.darts.common.client1.exeption.ClientProblemException;
import uk.gov.hmcts.darts.common.client1.exeption.JacksonFeignClientProblemDecoder;
import uk.gov.hmcts.darts.common.client1.mapper.APIProblemResponseMapper;
import uk.gov.hmcts.darts.common.exceptions.DartsException;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.utilities.TestUtils;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class ClientProblemDecoderTest {

    private Response response;


    private void setupSuccessResponse() throws IOException {
        response = Mockito.mock(Response.class);
        Response.Body body = Mockito.mock(Response.Body.class);

        Mockito.when(response.body()).thenReturn(body);

        String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "tests/client/error/problemResponse.json");

        Mockito.when(body.asInputStream()).thenReturn(new ByteArrayInputStream(dartsApiResponseStr.getBytes()));
    }

    @Test
    void testDecoderNoProblemMapperFound() throws IOException {
        setupSuccessResponse();

        APIProblemResponseMapper mapper = Mockito.mock(APIProblemResponseMapper.class);
        Mockito.when(mapper.getExceptionForProblem(Mockito.any(Problem.class))).thenReturn(Optional.empty());

        List<APIProblemResponseMapper> responseMappers = new ArrayList<>();
        responseMappers.add(mapper);

        Exception exception = new JacksonFeignClientProblemDecoder(responseMappers).decode("", response);

        Assertions.assertTrue(ClientProblemException.class.isAssignableFrom(exception.getClass()));
        Assertions.assertNotNull(((ClientProblemException) exception).getProblem());
        Assertions.assertEquals(CodeAndMessage.ERROR, ((ClientProblemException) exception).getCodeAndMessage());
    }

    @Test
    void testDecoderErrorsException() throws IOException {
        setupSuccessResponse();

        ClientProblemException exceptionToReturn = new ClientProblemException(null);
        APIProblemResponseMapper mapper = Mockito.mock(APIProblemResponseMapper.class);
        Mockito.when(mapper.getExceptionForProblem(Mockito.any(Problem.class))).thenReturn(Optional.of(exceptionToReturn));

        List<APIProblemResponseMapper> responseMappers = new ArrayList<>();
        responseMappers.add(mapper);

        Exception exception = new JacksonFeignClientProblemDecoder(responseMappers).decode("", response);
        Assertions.assertTrue(ClientProblemException.class.isAssignableFrom(exception.getClass()));
        Assertions.assertSame(exception, exceptionToReturn);
    }

    @Test
    void testDecoderProblemParsingException() throws IOException {
        response = Mockito.mock(Response.class);
        Response.Body body = Mockito.mock(Response.Body.class);

        Mockito.when(response.body()).thenReturn(body);

        String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "tests/client/error/invalidProblemResponse.json");

        Mockito.when(body.asInputStream()).thenReturn(new ByteArrayInputStream(dartsApiResponseStr.getBytes()));

        ClientProblemException exceptionToReturn = new ClientProblemException(null);
        APIProblemResponseMapper mapper = Mockito.mock(APIProblemResponseMapper.class);
        Mockito.when(mapper.getExceptionForProblem(Mockito.any(Problem.class))).thenReturn(Optional.of(exceptionToReturn));

        List<APIProblemResponseMapper> responseMappers = new ArrayList<>();
        responseMappers.add(mapper);

        Exception exception = new JacksonFeignClientProblemDecoder(responseMappers).decode("", response);
        Assertions.assertEquals(DartsException.class, exception.getClass());
        Assertions.assertEquals(CodeAndMessage.ERROR, ((DartsException) exception).getCodeAndMessage());
    }
}

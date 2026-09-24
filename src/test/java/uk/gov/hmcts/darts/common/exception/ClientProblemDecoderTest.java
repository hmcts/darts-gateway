package uk.gov.hmcts.darts.common.exception;

import feign.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.common.client.exeption.JacksonFeignClientProblemDecoder;
import uk.gov.hmcts.darts.common.client.mapper.APIProblemResponseMapper;
import uk.gov.hmcts.darts.common.exceptions.DartsException;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.utilities.TestUtils;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class ClientProblemDecoderTest {

    private static final String PROBLEM_RESPONSE_WITH_UNKNOWN_FIELDS = """
        {
          "type": "TEST_TYPE",
          "title": "A descriptive title",
          "status": 400,
          "detail": "A useful detail",
          "instance": "/admin/users/search",
          "properties": {
            "emailAddress": "size must be between 1 and 256"
          }
        }
        """;

    private static final String SPRING_BOOT_BAD_REQUEST_PROBLEM_RESPONSE = """
        {
          "type": "about:blank",
          "title": "Bad Request",
          "status": 400,
          "detail": "JSON parse error",
          "instance": "/events"
        }
        """;

    private Response response;


    private void setupSuccessResponse() throws IOException {
        setupResponse(TestUtils.getContentsFromFile("tests/client/error/problemResponse.json"));
    }

    private void setupResponse(String dartsApiResponseStr) throws IOException {
        response = Mockito.mock(Response.class);
        Response.Body body = Mockito.mock(Response.Body.class);

        Mockito.when(response.body()).thenReturn(body);

        Mockito.when(body.asInputStream()).thenReturn(new ByteArrayInputStream(dartsApiResponseStr.getBytes(StandardCharsets.UTF_8)));
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
        setupResponse(TestUtils.getContentsFromFile("tests/client/error/invalidProblemResponse.json"));

        ClientProblemException exceptionToReturn = new ClientProblemException(null);
        APIProblemResponseMapper mapper = Mockito.mock(APIProblemResponseMapper.class);
        Mockito.when(mapper.getExceptionForProblem(Mockito.any(Problem.class))).thenReturn(Optional.of(exceptionToReturn));

        List<APIProblemResponseMapper> responseMappers = new ArrayList<>();
        responseMappers.add(mapper);

        Exception exception = new JacksonFeignClientProblemDecoder(responseMappers).decode("", response);
        Assertions.assertEquals(ClientProblemException.class, exception.getClass());
        Assertions.assertEquals(CodeAndMessage.ERROR, ((DartsException) exception).getCodeAndMessage());
    }

    @Test
    void testDecoderHandlesProblemResponseWithUnknownFields() throws IOException {
        setupResponse(PROBLEM_RESPONSE_WITH_UNKNOWN_FIELDS);

        APIProblemResponseMapper mapper = Mockito.mock(APIProblemResponseMapper.class);
        Mockito.when(mapper.getExceptionForProblem(Mockito.any(Problem.class))).thenReturn(Optional.empty());

        List<APIProblemResponseMapper> responseMappers = new ArrayList<>();
        responseMappers.add(mapper);

        Exception exception = new JacksonFeignClientProblemDecoder(responseMappers).decode("", response);

        Assertions.assertEquals(ClientProblemException.class, exception.getClass());
        Problem problem = ((ClientProblemException) exception).getProblem();
        Assertions.assertEquals("TEST_TYPE", problem.getType());
        Assertions.assertEquals("A descriptive title", problem.getTitle());
        Assertions.assertEquals(400, problem.getStatus());
        Assertions.assertEquals("A useful detail", problem.getDetail());
    }

    @Test
    void testDecoderMapsSpringBootBadRequestProblemResponseToInvalidXml() throws IOException {
        setupResponse(SPRING_BOOT_BAD_REQUEST_PROBLEM_RESPONSE);

        APIProblemResponseMapper mapper = Mockito.mock(APIProblemResponseMapper.class);
        Mockito.when(mapper.getExceptionForProblem(Mockito.any(Problem.class))).thenReturn(Optional.empty());

        List<APIProblemResponseMapper> responseMappers = new ArrayList<>();
        responseMappers.add(mapper);

        Exception exception = new JacksonFeignClientProblemDecoder(responseMappers).decode("", response);

        Assertions.assertEquals(ClientProblemException.class, exception.getClass());
        Assertions.assertEquals(CodeAndMessage.INVALID_XML, ((ClientProblemException) exception).getCodeAndMessage());
    }
}

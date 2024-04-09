package uk.gov.hmcts.darts.common.client.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.common.client.exeption.dailylist.DailyListAPIAddException;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.model.common.CommonErrorCode;
import uk.gov.hmcts.darts.model.dailylist.PostDailyListErrorCode;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.net.URI;
import java.util.Optional;

class APIResponseMapperTest {

    @Test
    void testNoProblemFound() {
        Problem problem = new Problem();
        URI uriType = URI.create("noproblem");
        problem.setType(uriType);
        Optional<? extends ProblemResponseMapping<?>> mapping = new DummyAPIResponseMapper().getMapping(problem);
        Assertions.assertFalse(mapping.isPresent());
    }

    @Test
    void testProblemNoCourtHouseException() {
        Problem problem = new Problem();
        CommonErrorCode problemCode = CommonErrorCode.COURTHOUSE_PROVIDED_DOES_NOT_EXIST;
        URI uriType = URI.create(problemCode.getValue());
        problem.setType(uriType);
        Optional<ClientProblemException> exception = new DummyAPIResponseMapper().getExceptionForProblem(problem);
        Assertions.assertTrue(exception.isPresent());
        Assertions.assertEquals(CodeAndMessage.NOT_FOUND_COURTHOUSE, exception.get().getCodeAndMessage());
        Assertions.assertEquals(problem, exception.get().getProblem());
    }

    @SuppressWarnings({"unchecked", "PMD.NonStaticInitializer"})
    class DummyAPIResponseMapper extends AbstractAPIProblemResponseMapper {
        {
            ProblemResponseMappingOperation<CommonErrorCode> opmapping = new ProblemResponseMappingOperation
                .ProblemResponseMappingOperationBuilder<CommonErrorCode>()
                .operation(CommonErrorCode.class)
                .exception((mapping) -> new DailyListAPIAddException(
                    (ProblemResponseMapping<PostDailyListErrorCode>) mapping.getMapping(), mapping.getProblem())).build();

            opmapping.addMapping(opmapping.createProblemResponseMapping()
                                     .problem(CommonErrorCode.COURTHOUSE_PROVIDED_DOES_NOT_EXIST)
                                     .message(CodeAndMessage.NOT_FOUND_COURTHOUSE).build());

            addOperationMappings(opmapping);
        }
    }
}

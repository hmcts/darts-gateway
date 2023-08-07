package uk.gov.hmcts.darts.cases.impl;

import com.service.mojdarts.synapps.com.addcase.AddCaseResponse;
import com.service.mojdarts.synapps.com.addcase.Case;
import com.service.mojdarts.synapps.com.addcase.NewDataSet;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.darts.cases.mapper.AddCaseMapper;
import uk.gov.hmcts.darts.common.client.DartsFeignClient;
import uk.gov.hmcts.darts.model.cases.ScheduledCase;
import uk.gov.hmcts.darts.ws.DartsResponseUtils;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CasesRouteImplTest {


    public static final String CASE_NUMBER = "Case01";
    public static final String COURTHOUSE = "SWANSEA";
    public static final String COURTROOM = "1";
    @Mock
    private DartsFeignClient dartsFeignClient;

    private CasesRouteImpl casesRoute;

    @NotNull
    private static NewDataSet createAddRequest() {
        Case caseRequest = new Case();
        caseRequest.setCourthouse(COURTHOUSE);
        caseRequest.setCourtroom(COURTROOM);

        Case.Defendants defendants = new Case.Defendants();
        defendants.getDefendant().add("Defendant1");
        caseRequest.getDefendants().add(defendants);


        Case.Judges judge = new Case.Judges();
        judge.getJudge().add("Judge1");
        caseRequest.getJudges().add(judge);

        Case.Prosecutors prosecutor = new Case.Prosecutors();
        prosecutor.getProsecutor().add("Prosecutor1");
        caseRequest.getProsecutors().add(prosecutor);

        Case.Defenders defenders = new Case.Defenders();
        defenders.getDefender().add("Defence1");
        caseRequest.getDefenders().add(defenders);

        NewDataSet addCaseRequest = new NewDataSet();
        caseRequest.setId(CASE_NUMBER);
        addCaseRequest.getCase().add(caseRequest);

        return addCaseRequest;
    }

    @BeforeEach
    void setUp() {
        AddCaseMapper addCaseMapper = new AddCaseMapper();
        casesRoute = new CasesRouteImpl(dartsFeignClient, addCaseMapper, new DartsResponseUtils());
    }

    @Test
    void addCase() {
        Mockito.when(dartsFeignClient.addCase(any())).thenReturn(new ScheduledCase());
        NewDataSet addCaseRequest = createAddRequest();

        casesRoute.addCase(addCaseRequest);

        ArgumentCaptor<uk.gov.hmcts.darts.model.cases.AddCaseRequest> captor
            = ArgumentCaptor.forClass(uk.gov.hmcts.darts.model.cases.AddCaseRequest.class);

        Mockito.verify(dartsFeignClient).addCase(captor.capture());

        uk.gov.hmcts.darts.model.cases.AddCaseRequest value = captor.getValue();
        assertEquals(CASE_NUMBER, value.getCaseNumber());
        assertEquals(COURTHOUSE, value.getCourthouse());

        assert value.getDefenders() != null;
        assertEquals(1, value.getDefenders().size());

        assert value.getDefendants() != null;
        assertEquals(1, value.getDefendants().size());

        assert value.getJudges() != null;
        assertEquals(1, value.getJudges().size());

        assert value.getProsecutors() != null;
        assertEquals(1, value.getProsecutors().size());
    }

    @Test
    void addCaseWithError() {
        Request request = Request.create(Request.HttpMethod.GET, "url",
                                         new HashMap<>(), null, new RequestTemplate()
        );
        Mockito.when(dartsFeignClient.addCase(any())).thenThrow(new FeignException.NotFound(
            "not found",
            request,
            null,
            null
        ));
        NewDataSet addCaseRequest = createAddRequest();

        AddCaseResponse addCaseResponse = casesRoute.addCase(addCaseRequest);

        assertEquals("500", addCaseResponse.getReturn().getCode());
        assertEquals("not found", addCaseResponse.getReturn().getMessage());
    }
}

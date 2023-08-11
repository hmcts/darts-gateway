package uk.gov.hmcts.darts.cases.mapper;

import com.service.mojdarts.synapps.com.addcase.Case;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddCaseMapperTest {

    public static final String CASE_NUMBER = "Case001";
    public static final String COURTHOUSE = "SWANSEA";
    public static final String COURTROOM = "1";

    AddCaseMapper addCaseMapper;


    @BeforeEach
    public void setUp() {
        addCaseMapper = new AddCaseMapper();
    }

    @Test
    void mapToMojDartsAddCaseRequest() {
        Case caseRequest = new Case();
        caseRequest.setId(CASE_NUMBER);

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

        uk.gov.hmcts.darts.model.cases.AddCaseRequest result = addCaseMapper.mapToDartsApi(caseRequest);
        assertEquals(COURTHOUSE, result.getCourthouse());
        assertEquals(CASE_NUMBER, result.getCaseNumber());

        assert result.getDefendants() != null;
        assertEquals(1, result.getDefendants().size());

        assert result.getJudges() != null;
        assertEquals(1, result.getJudges().size());

        assert result.getProsecutors() != null;
        assertEquals(1, result.getProsecutors().size());

        assert result.getDefenders() != null;
        assertEquals(1, result.getDefenders().size());
    }
}

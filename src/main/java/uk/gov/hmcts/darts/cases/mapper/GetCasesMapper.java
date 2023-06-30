package uk.gov.hmcts.darts.cases.mapper;

import com.service.mojdarts.synapps.com.GetCases;
import com.service.mojdarts.synapps.com.GetCasesResponse;
import com.synapps.moj.dfs.response.Case;
import com.synapps.moj.dfs.response.Cases;
import com.synapps.moj.dfs.response.Defendants;
import com.synapps.moj.dfs.response.Defenders;
import com.synapps.moj.dfs.response.Judges;
import com.synapps.moj.dfs.response.Prosecutors;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import uk.gov.hmcts.darts.model.cases.ScheduledCase;

import java.time.LocalDate;
import java.util.List;

@UtilityClass
public class GetCasesMapper {

    public GetCasesResponse mapToMojDartsResponse(GetCases getCasesRequest, List<ScheduledCase> modernisedDartsResponse) {
        GetCasesResponse getCasesResponse = new GetCasesResponse();
        getCasesResponse.setReturn(mapToDfsResponse(getCasesRequest, modernisedDartsResponse));
        return getCasesResponse;
    }

    private com.synapps.moj.dfs.response.GetCasesResponse mapToDfsResponse(GetCases getCasesRequest, List<ScheduledCase> modernisedDartsResponse) {
        Cases cases = new Cases();
        setCasesAttributes(getCasesRequest, cases);
        cases.setCourthouse(getCasesRequest.getCourthouse());
        cases.setCourthouse(getCasesRequest.getCourthouse());
        cases.setCourthouse(getCasesRequest.getCourthouse());
        cases.setCourthouse(getCasesRequest.getCourthouse());
        List<Case> caseList = cases.getCase();
        for (ScheduledCase scheduledCase : modernisedDartsResponse) {
            caseList.add(mapToCase(scheduledCase));
        }

        com.synapps.moj.dfs.response.GetCasesResponse response = new com.synapps.moj.dfs.response.GetCasesResponse();
        response.setCases(cases);
        return response;
    }

    private static void setCasesAttributes(GetCases getCasesRequest, Cases cases) {
        cases.setCourthouse(getCasesRequest.getCourthouse());
        cases.setCourtroom(getCasesRequest.getCourtroom());
        LocalDate localDate = LocalDate.parse(getCasesRequest.getDate());
        cases.setY(String.valueOf(localDate.getYear()));
        cases.setM(String.valueOf(localDate.getMonthValue()));
        cases.setD(String.valueOf(localDate.getDayOfMonth()));
    }

    private Case mapToCase(ScheduledCase scheduledCase) {
        Case newCase = new Case();
        newCase.setCaseNumber(scheduledCase.getCaseNumber());
        newCase.setDefendants(createDefendants(scheduledCase.getDefendants()));
        newCase.setDefenders(createDefenders(scheduledCase.getDefenders()));
        newCase.setJudges(createJudges(scheduledCase.getJudges()));
        newCase.setProsecutors(createProsecutors(scheduledCase.getProsecutors()));
        newCase.setScheduledStart(scheduledCase.getScheduledStart());
        newCase.setUploadPriority(StringUtils.defaultIfBlank(scheduledCase.getUploadPriority(), ""));
        return newCase;
    }

    private Defendants createDefendants(List<String> defendantList) {
        Defendants defendants = new Defendants();
        List<String> newDefendants = defendants.getDefendant();
        newDefendants.addAll(defendantList);
        return defendants;
    }

    private Defenders createDefenders(List<String> defenderList) {
        Defenders defenders = new Defenders();
        List<String> newDefenders = defenders.getDefender();
        newDefenders.addAll(defenderList);
        return defenders;
    }

    private Judges createJudges(List<String> defenderList) {
        Judges judges = new Judges();
        List<String> newJudges = judges.getJudge();
        newJudges.addAll(defenderList);
        return judges;
    }

    private Prosecutors createProsecutors(List<String> defenderList) {
        Prosecutors prosecutors = new Prosecutors();
        List<String> newProsecutors = prosecutors.getProsecutor();
        newProsecutors.addAll(defenderList);
        return prosecutors;
    }
}

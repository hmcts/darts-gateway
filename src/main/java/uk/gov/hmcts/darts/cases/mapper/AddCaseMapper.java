package uk.gov.hmcts.darts.cases.mapper;

import com.service.mojdarts.synapps.com.addcase.Case;
import com.service.mojdarts.synapps.com.addcase.NewDataSet;
import org.springframework.stereotype.Service;

@Service
public class AddCaseMapper {
    public uk.gov.hmcts.darts.model.cases.AddCaseRequest mapToMojDartsAddCaseRequest(NewDataSet soapRequest) {

        uk.gov.hmcts.darts.model.cases.AddCaseRequest mojDartsRequest = new uk.gov.hmcts.darts.model.cases.AddCaseRequest();
        Case caseRequest = soapRequest.getCase().get(0);
        mojDartsRequest.setCourthouse(caseRequest.getCourthouse());
        mojDartsRequest.setCourtroom(caseRequest.getCourtroom());
        mojDartsRequest.setCaseNumber(caseRequest.getId());

        caseRequest.getDefendants().stream().findFirst().ifPresent(defendantList -> mojDartsRequest.setDefendants(
            defendantList.getDefendant()));

        caseRequest.getJudges().stream().findFirst().ifPresent(judgeList -> mojDartsRequest.setJudges(judgeList.getJudge()));

        caseRequest.getProsecutors().stream().findFirst().ifPresent(prosecutorList -> mojDartsRequest.setProsecutors(
            prosecutorList.getProsecutor()));

        caseRequest.getDefenders().stream().findFirst().ifPresent(defendersList -> mojDartsRequest.setDefenders(
            defendersList.getDefender()));

        return mojDartsRequest;


    }


}

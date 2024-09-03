package uk.gov.hmcts.darts.cases.mapper;

import com.service.mojdarts.synapps.com.addcase.Case;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.model.cases.AddCaseRequest;

@Service
public class AddCaseMapper {

    public AddCaseRequest mapToDartsApi(Case legacyCase) {
        var addCaseRequest = new AddCaseRequest();
        addCaseRequest.setCourthouse(legacyCase.getCourthouse());
        addCaseRequest.setCaseNumber(legacyCase.getId());
        addCaseRequest.setCaseType(legacyCase.getType());

        legacyCase.getDefendants().stream()
            .findFirst()
            .ifPresent(defendantList -> addCaseRequest.setDefendants(defendantList.getDefendant()));

        legacyCase.getJudges().stream()
            .findFirst()
            .ifPresent(judgeList -> addCaseRequest.setJudges(judgeList.getJudge()));

        legacyCase.getProsecutors().stream()
            .findFirst()
            .ifPresent(prosecutorList -> addCaseRequest.setProsecutors(prosecutorList.getProsecutor()));

        legacyCase.getDefenders().stream().findFirst().ifPresent(defendersList -> addCaseRequest.setDefenders(
            defendersList.getDefender()));

        return addCaseRequest;
    }
}

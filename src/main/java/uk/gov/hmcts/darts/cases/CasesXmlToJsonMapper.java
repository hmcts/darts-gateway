package uk.gov.hmcts.darts.cases;

import org.springframework.stereotype.Service;
import uk.gov.courtservice.addcase.Case;

import java.util.List;

@Service
public class CasesXmlToJsonMapper {

    public AddCaseRequest toJson(Case caze) {
        return new AddCaseRequest(
            caze.getCourthouse(),
            caze.getCourtroom(),
            defendantsFrom(caze),
            judgesFrom(caze),
            prosecutorsFrom(caze),
            defendersFrom(caze),
            caze.getType(),
            caze.getId()
        );
    }

    private static List<String> defendersFrom(Case caze) {
        return caze.getDefenders().stream()
            .flatMap(d -> d.getDefender().stream())
            .toList();
    }

    private static List<String> prosecutorsFrom(Case caze) {
        return caze.getProsecutors().stream()
            .flatMap(p -> p.getProsecutor().stream())
            .toList();
    }

    private static List<String> judgesFrom(Case caze) {
        return caze.getJudges().stream()
            .flatMap(j -> j.getJudge().stream())
            .toList();
    }

    private static List<String> defendantsFrom(Case caze) {
        return caze.getDefendants().stream()
            .flatMap(d -> d.getDefendant().stream())
            .toList();
    }
}

package uk.gov.hmcts.darts.cases;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.courtservice.addcase.Case;
import uk.gov.hmcts.darts.utilities.XmlParser;

@Service
@AllArgsConstructor
public class AddCaseRoute {

    private final CasesApiClient casesApiClient;
    private final XmlParser xmlParser;
    private final CasesXmlToJsonMapper casesXmlMapper;

    public void route(String document) {
        Case addCase = xmlParser.unmarshal(document, Case.class);
        casesApiClient.addCase(
            casesXmlMapper.toJson(addCase));
    }
}

package uk.gov.hmcts.darts.cases.impl;

import com.service.mojdarts.synapps.com.AddCase;
import com.service.mojdarts.synapps.com.GetCases;
import com.service.mojdarts.synapps.com.GetCasesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.cases.CasesRoute;
import uk.gov.hmcts.darts.cases.mapper.AddCaseMapper;
import uk.gov.hmcts.darts.cases.mapper.GetCasesMapper;
import uk.gov.hmcts.darts.common.client.DartsFeignClient;
import uk.gov.hmcts.darts.model.cases.ScheduledCase;
import uk.gov.hmcts.darts.utilities.XmlParser;
import uk.gov.hmcts.darts.utilities.XmlValidator;
import com.service.mojdarts.synapps.com.addcase.Case;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CasesRouteImpl implements CasesRoute {

    @Value("${darts-gateway.addcase.schema}")
    private String addCaseSchemaPath;
    @Value("${darts-gateway.addcase.validate}")
    private boolean validateAddCase;
    private final XmlValidator xmlValidator;
    private final DartsFeignClient dartsFeignClient;
    private final XmlParser xmlParser;
    private final AddCaseMapper addCaseMapper;

    @Override
    public GetCasesResponse route(GetCases getCasesRequest) {

        List<ScheduledCase> modernisedDartsResponse = dartsFeignClient.getCases(
            getCasesRequest.getCourthouse(),
            getCasesRequest.getCourtroom(),
            getCasesRequest.getDate()
        );
        return GetCasesMapper.mapToMojDartsResponse(getCasesRequest, modernisedDartsResponse);
    }

    @Override
    public void route(AddCase addCase) {
        var caseDocumentXmlStr = addCase.getDocument();
        if (validateAddCase) {
            xmlValidator.validate(caseDocumentXmlStr, addCaseSchemaPath);
        }

        var caseDocument = xmlParser.unmarshal(caseDocumentXmlStr, Case.class);
        var addCaseRequest = addCaseMapper.mapToDartsApi(caseDocument);

        dartsFeignClient.addCase(addCaseRequest);
    }
}

package uk.gov.hmcts.darts.cases.impl;

import com.service.mojdarts.synapps.com.AddCase;
import com.service.mojdarts.synapps.com.GetCases;
import com.service.mojdarts.synapps.com.addcase.Case;
import com.synapps.moj.dfs.response.DARTSResponse;
import com.synapps.moj.dfs.response.GetCasesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.cases.CasesRoute;
import uk.gov.hmcts.darts.cases.mapper.AddCaseMapper;
import uk.gov.hmcts.darts.cases.mapper.GetCasesMapper;
import uk.gov.hmcts.darts.common.client.CasesClient;
import uk.gov.hmcts.darts.model.cases.AddCaseRequest;
import uk.gov.hmcts.darts.model.cases.ScheduledCase;
import uk.gov.hmcts.darts.utilities.DateUtil;
import uk.gov.hmcts.darts.utilities.XmlParser;
import uk.gov.hmcts.darts.utilities.XmlValidator;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CasesRouteImpl implements CasesRoute {

    @Value("${darts-gateway.addcase.schema}")
    private String addCaseSchemaPath;
    @Value("${darts-gateway.addcase.validate}")
    private boolean validateAddCase;
    private final XmlValidator xmlValidator;
    private final XmlParser xmlParser;
    private final AddCaseMapper addCaseMapper;

    private final CasesClient casesClient;


    @Override
    public GetCasesResponse route(GetCases getCasesRequest) {
        String dateString = getCasesRequest.getDate();
        LocalDate localDate = DateUtil.toLocalDate(dateString);
        ResponseEntity<List<ScheduledCase>> modernisedDartsResponse = casesClient.casesGet(
            getCasesRequest.getCourthouse(),
            getCasesRequest.getCourtroom(),
            localDate
        );
        return GetCasesMapper.mapToDfsResponse(modernisedDartsResponse.getBody());
    }

    @Override
    public DARTSResponse route(AddCase addCase) {
        String caseDocumentXmlStr = addCase.getDocument();
        if (validateAddCase) {
            xmlValidator.validate(caseDocumentXmlStr, addCaseSchemaPath);
        }

        Case caseDocument = xmlParser.unmarshal(caseDocumentXmlStr, Case.class);
        AddCaseRequest addCaseRequest = addCaseMapper.mapToDartsApi(caseDocument);

        casesClient.casesPost(addCaseRequest);

        CodeAndMessage okResponse = CodeAndMessage.OK;
        return okResponse.getResponse();
    }
}

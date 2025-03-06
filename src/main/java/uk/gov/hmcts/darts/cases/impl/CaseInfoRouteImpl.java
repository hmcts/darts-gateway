package uk.gov.hmcts.darts.cases.impl;

import com.service.mojdarts.synapps.com.caseinfo.CaseStructure;
import com.service.mojdarts.synapps.com.caseinfo.CitizenNameStructure;
import com.service.mojdarts.synapps.com.caseinfo.NewCaseMessageStructure;
import com.service.mojdarts.synapps.com.caseinfo.UpdatedCaseMessageStructure;
import com.synapps.moj.dfs.response.DARTSResponse;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.cases.CaseInfoRoute;
import uk.gov.hmcts.darts.common.client.CasesClient;
import uk.gov.hmcts.darts.model.cases.AddCaseRequest;
import uk.gov.hmcts.darts.utilities.DataUtil;
import uk.gov.hmcts.darts.utilities.XmlParser;
import uk.gov.hmcts.darts.utilities.XmlValidator;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CaseInfoRouteImpl implements CaseInfoRoute {

    private static final String NEW_CASE_TYPE = "NEWCASE";
    private static final String UPDATE_CASE_TYPE = "UPDCASE";
    private final XmlValidator xmlValidator;
    private final CasesClient casesClient;


    @Value("${darts-gateway.case-info.schema}")
    private String schemaPath;
    @Value("${darts-gateway.case-info.validate}")
    private boolean validate;


    @Override
    public DARTSResponse handle(String document, String type) {
        if (validate) {
            xmlValidator.validate(document, schemaPath);
        }
        CaseStructure caseStructure;
        if (NEW_CASE_TYPE.equals(type)) {
            NewCaseMessage newCaseMessage = XmlParser.unmarshal(document, NewCaseMessage.class);
            caseStructure = newCaseMessage.getCase();
        } else if (UPDATE_CASE_TYPE.equals(type)) {
            UpdatedCaseMessage updatedCaseMessage = XmlParser.unmarshal(document, UpdatedCaseMessage.class);
            caseStructure = updatedCaseMessage.getCase();
        } else {
            throw new UnsupportedOperationException("Unsupported type: " + type);
        }
        casesClient.casesPost(mapToAddCaseRequest(caseStructure));
        return CodeAndMessage.OK.getResponse();
    }


    AddCaseRequest mapToAddCaseRequest(CaseStructure request) {
        AddCaseRequest addCaseRequest = new AddCaseRequest();
        addCaseRequest.setCourthouse(request.getCourt().getCourtHouseName());
        addCaseRequest.setCaseNumber(request.getCaseNumber());
        addCaseRequest.setDefenders(
            request.getDefendants().getDefendant()
                .stream()
                .map(defendantStructure -> mapToName(defendantStructure.getPersonalDetails().getName()))
                .toList());
        return addCaseRequest;
    }

    String mapToName(CitizenNameStructure citizenName) {
        List<String> nameSegments = new ArrayList<>(citizenName.getCitizenNameForename());
        nameSegments.add(citizenName.getCitizenNameSurname());
        return DataUtil.trim(
            nameSegments.stream()
                .map(DataUtil::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(" ")));
    }

    @XmlRootElement(name = "NewCaseMessage", namespace = "http://www.hmcs.gov.uk/schemas/crowncourt/msg")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class NewCaseMessage extends NewCaseMessageStructure {

    }

    @XmlRootElement(name = "UpdatedCaseMessage", namespace = "http://www.hmcs.gov.uk/schemas/crowncourt/msg")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class UpdatedCaseMessage extends UpdatedCaseMessageStructure {

    }
}

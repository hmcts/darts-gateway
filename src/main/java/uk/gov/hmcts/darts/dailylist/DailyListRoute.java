package uk.gov.hmcts.darts.dailylist;

import com.service.mojdarts.synapps.com.AddDocumentResponse;
import com.synapps.moj.dfs.response.DARTSResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.courtservice.schemas.courtservice.DailyListStructure;
import uk.gov.hmcts.darts.common.client.DartsFeignClient;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;
import uk.gov.hmcts.darts.dailylist.enums.SystemType;
import uk.gov.hmcts.darts.dailylist.mapper.DailyListRequestMapper;
import uk.gov.hmcts.darts.model.dailyList.DailyList;
import uk.gov.hmcts.darts.utilities.XmlParser;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DailyListRoute {

    private final DartsFeignClient dartsFeignClient;
    private final XmlParser xmlParser;
    private final DailyListRequestMapper dailyListRequestMapper;

    public AddDocumentResponse handle(String document, String type) {
        Optional<SystemType> systemType = SystemType.getByType(type);
        if (systemType.isEmpty()) {
            throw new DartsValidationException("SystemType is not valid " + type);
        }
        DailyListStructure legacyDailyList = xmlParser.unmarshal(document, DailyListStructure.class);
        DailyList modernisedDailyList = dailyListRequestMapper.mapToEntity(legacyDailyList);
        dartsFeignClient.postDailyLists(
            systemType.get().getModernisedSystemType(),
            modernisedDailyList
        );
        return successResponse();
    }

    private AddDocumentResponse successResponse() {
        DARTSResponse dartsResponse = new DARTSResponse();
        dartsResponse.setCode("200");
        dartsResponse.setMessage("OK");
        AddDocumentResponse response = new AddDocumentResponse();
        response.setReturn(dartsResponse);
        return response;
    }
}

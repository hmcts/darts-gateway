package uk.gov.hmcts.darts.dailylist;

import com.service.mojdarts.synapps.com.AddDocumentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.courtservice.schemas.courtservice.DailyListStructure;
import uk.gov.hmcts.darts.common.client.DartsFeignClient;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;
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

    public AddDocumentResponse handle(String document, String messageId, String type) {
        Optional<SystemType> systemType = SystemType.getByType(type);
        if (systemType.isEmpty()) {
            throw new DartsValidationException("SystemType is not valid " + type);
        }
        DailyListStructure legacyDailyList = xmlParser.unmarshal(document, DailyListStructure.class);
        DailyList modernisedDailyList = dailyListRequestMapper.mapToEntity(legacyDailyList);
        dartsFeignClient.postDailyLists(systemType.get().getModernisedSystemType(), modernisedDailyList);
        return new AddDocumentResponse();
    }
}

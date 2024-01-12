package uk.gov.hmcts.darts.dailylist;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synapps.moj.dfs.response.DARTSResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.courtservice.schemas.courtservice.DailyListStructure;
import uk.gov.hmcts.darts.common.client.DailyListsClient;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;
import uk.gov.hmcts.darts.config.ServiceConfig;
import uk.gov.hmcts.darts.dailylist.enums.SystemType;
import uk.gov.hmcts.darts.dailylist.mapper.DailyListRequestMapper;
import uk.gov.hmcts.darts.dailylist.mapper.DailyListXmlRequestMapper;
import uk.gov.hmcts.darts.dailylist.model.PostDailyListRequest;
import uk.gov.hmcts.darts.model.dailylist.DailyListJsonObject;
import uk.gov.hmcts.darts.model.dailylist.PostDailyListResponse;
import uk.gov.hmcts.darts.utilities.XmlParser;
import uk.gov.hmcts.darts.utilities.XmlValidator;
import uk.gov.hmcts.darts.utilities.deserializer.LocalDateTypeDeserializer;
import uk.gov.hmcts.darts.utilities.deserializer.OffsetDateTimeTypeDeserializer;
import uk.gov.hmcts.darts.ws.CodeAndMessage;
import uk.gov.hmcts.darts.common.exceptions.DartsException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyListRoute {

    private final DailyListsClient dailyListsClient;
    private final XmlParser xmlParser;
    private final DailyListRequestMapper dailyListRequestMapper;
    private final XmlValidator xmlValidator;

    @Value("${darts-gateway.daily-list.schema}")
    private String schemaPath;
    @Value("${darts-gateway.daily-list.validate}")
    private boolean validate;

    public DARTSResponse handle(String document, String type) {
        Optional<SystemType> systemType = SystemType.getByType(type);
        if (systemType.isEmpty()) {
            throw new DartsValidationException((Throwable) null, CodeAndMessage.SYSTEM_TYPE_NOT_FOUND);
        }

        if (validate) {
            try {
                xmlValidator.validate(document, schemaPath);
            } catch (DartsValidationException dve) {
                log.error("Exception occurred during validation od XML", dve);
                throw new DartsException(dve, CodeAndMessage.INVALID_XML);
            }
        }

        DailyListStructure legacyDailyListObject = xmlParser.unmarshal(document.trim(), DailyListStructure.class);

        DailyListJsonObject modernisedDailyList = dailyListRequestMapper.mapToEntity(legacyDailyListObject);

        PostDailyListRequest postDailyListRequest = DailyListXmlRequestMapper.mapToPostDailyListRequest(
                legacyDailyListObject,
                document
        );

        ResponseEntity<PostDailyListResponse> postDailyListResponse = dailyListsClient.dailylistsPost(
            systemType.get().getModernisedSystemType(),
            postDailyListRequest.getCourthouse(),
            LocalDateTypeDeserializer.getLocalDate(postDailyListRequest.getHearingDate()),
            postDailyListRequest.getUniqueId(),
            OffsetDateTimeTypeDeserializer.getLOffsetDate(postDailyListRequest.getPublishedTs()),
            postDailyListRequest.getDailyListXml(),
            null
        );

        Integer dalId = postDailyListResponse.getBody().getDalId();

        String modernisedDailyListJson;
        try {
            modernisedDailyListJson = ServiceConfig.getServiceObjectMapper().writeValueAsString(modernisedDailyList);
        } catch (JsonProcessingException ex) {
            throw new DartsException(ex, CodeAndMessage.INVALID_XML);
        }

        dailyListsClient.dailylistsPatch(
                dalId,
                modernisedDailyListJson
        );
        return successResponse();
    }

    private DARTSResponse successResponse() {
        DARTSResponse dartsResponse = new DARTSResponse();
        dartsResponse.setCode("200");
        dartsResponse.setMessage("OK");
        return dartsResponse;
    }
}

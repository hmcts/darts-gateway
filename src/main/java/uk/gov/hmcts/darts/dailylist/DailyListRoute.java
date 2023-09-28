package uk.gov.hmcts.darts.dailylist;

import com.synapps.moj.dfs.response.DARTSResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.courtservice.schemas.courtservice.DailyListStructure;
import uk.gov.hmcts.darts.common.client.DailyListsClient;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;
import uk.gov.hmcts.darts.dailylist.enums.SystemType;
import uk.gov.hmcts.darts.dailylist.mapper.DailyListRequestMapper;
import uk.gov.hmcts.darts.dailylist.mapper.DailyListXmlRequestMapper;
import uk.gov.hmcts.darts.dailylist.model.PostDailyListRequest;
import uk.gov.hmcts.darts.model.dailylist.CourtHouse;
import uk.gov.hmcts.darts.model.dailylist.DailyListJsonObject;
import uk.gov.hmcts.darts.model.dailylist.DocumentID;
import uk.gov.hmcts.darts.model.dailylist.PostDailyListResponse;
import uk.gov.hmcts.darts.utilities.XmlParser;
import uk.gov.hmcts.darts.utilities.XmlValidator;
import uk.gov.hmcts.darts.utilities.deserializer.LocalDateTypeDeserializer;
import uk.gov.hmcts.darts.utilities.deserializer.OffsetDateTimeTypeDeserializer;
import uk.gov.hmcts.darts.ws.CodeAndMessage;
import uk.gov.hmcts.darts.ws.DartsException;

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
            throw new DartsValidationException("SystemType is not valid " + type);
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

        //TODO: Need to change the specification to take a string and then uncomment this section
        /*
        DailyListJsonObject modernisedDailyList = dailyListRequestMapper.mapToEntity(legacyDailyListObject);

        String modernisedDailyListJson;
        try {
            modernisedDailyListJson = ServiceConfig.getServiceObjectMapper().writeValueAsString(modernisedDailyList);

            //TODO: Need to validate against the Json schema here when it is split from the
            // openapispec
        } catch (JsonProcessingException ex) {
            throw new DartsException(ex, CodeAndMessage.INVALID_XML);
        }
        */

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

            //TODO: Need to change the specification to take a string and then pass modernisedDailyListJson. Feign will
            // not work with objects passed as headers
            null
        );

        DailyListJsonObject dailyListJsonObject = new DailyListJsonObject();
        CourtHouse courtHouse = new CourtHouse();
        courtHouse.setCourtHouseType("");
        courtHouse.setCourtHouseName("");

        dailyListJsonObject.setCrownCourt(courtHouse);
        dailyListJsonObject.setDocumentId(new DocumentID());

        Integer dalId = postDailyListResponse.getBody().getDalId();
        // TODO: We need to ensure we pass a string for the json as for the reasons below
        //dailyListsClient.dailylistsPatch(
        //    dalId,
            //TODO: Need to change the specification to take a string and then pass modernisedDailyListJson. Feign will
            // not work with objects passed as headers
        //    dailyListJsonObject
        //);
        return successResponse();
    }

    private DARTSResponse successResponse() {
        DARTSResponse dartsResponse = new DARTSResponse();
        dartsResponse.setCode("200");
        dartsResponse.setMessage("OK");
        return dartsResponse;
    }
}

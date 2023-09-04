package uk.gov.hmcts.darts.dailylist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.service.mojdarts.synapps.com.AddDocumentResponse;
import com.synapps.moj.dfs.response.DARTSResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.courtservice.schemas.courtservice.DailyListStructure;
import uk.gov.hmcts.darts.common.client.DartsFeignClient;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;
import uk.gov.hmcts.darts.dailylist.enums.SystemType;
import uk.gov.hmcts.darts.dailylist.mapper.DailyListRequestMapper;
import uk.gov.hmcts.darts.dailylist.mapper.DailyListXmlRequestMapper;
import uk.gov.hmcts.darts.dailylist.model.PostDailyListRequest;
import uk.gov.hmcts.darts.model.dailylist.DailyListJsonObject;
import uk.gov.hmcts.darts.model.dailylist.PostDailyListResponse;
import uk.gov.hmcts.darts.utilities.LocalDateTimeTypeAdapter;
import uk.gov.hmcts.darts.utilities.LocalDateTypeAdapter;
import uk.gov.hmcts.darts.utilities.OffsetDateTimeTypeAdapter;
import uk.gov.hmcts.darts.utilities.XmlParser;
import uk.gov.hmcts.darts.utilities.XmlValidator;
import uk.gov.hmcts.darts.ws.CodeAndMessage;
import uk.gov.hmcts.darts.ws.DartsException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyListRoute {

    private final DartsFeignClient dartsFeignClient;
    private final XmlParser xmlParser;
    private final DailyListRequestMapper dailyListRequestMapper;
    private final XmlValidator xmlValidator;

    @Value("${darts-gateway.daily-list.schema}")
    private String schemaPath;
    @Value("${darts-gateway.daily-list.validate}")
    private boolean validate;

    public AddDocumentResponse handle(String document, String type) {
        Optional<SystemType> systemType = SystemType.getByType(type);
        if (systemType.isEmpty()) {
            throw new DartsValidationException("SystemType is not valid " + type);
        }

        if (validate) {
            try {
                xmlValidator.validate(document, schemaPath);
            } catch (DartsValidationException dve){
                log.error("Exception occurred during validation od XML", dve);
                throw new DartsException( dve, CodeAndMessage.INVALID_XML);
            }
        }
        DailyListStructure legacyDailyListObject = xmlParser.unmarshal(document, DailyListStructure.class);
        PostDailyListRequest postDailyListRequest = DailyListXmlRequestMapper.mapToPostDailyListRequest(
            legacyDailyListObject,
            document
        );
        PostDailyListResponse postDailyListResponse = dartsFeignClient.postDailyLists(
            systemType.get().getModernisedSystemType(),
            postDailyListRequest.getCourthouse(),
            postDailyListRequest.getHearingDate(),
            postDailyListRequest.getUniqueId(),
            postDailyListRequest.getPublishedTs(),
            postDailyListRequest.getDailyListXml()
        );

        Integer dalId = postDailyListResponse.getDalId();
        DailyListJsonObject modernisedDailyList = dailyListRequestMapper.mapToEntity(legacyDailyListObject);

        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeTypeAdapter())
            .create();
        dartsFeignClient.patchDailyLists(
            dalId,
            gson.toJson(modernisedDailyList)
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

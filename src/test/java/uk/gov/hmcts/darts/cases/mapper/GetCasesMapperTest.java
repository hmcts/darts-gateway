package uk.gov.hmcts.darts.cases.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.service.mojdarts.synapps.com.GetCases;
import com.synapps.moj.dfs.response.GetCasesResponse;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import uk.gov.hmcts.darts.model.cases.ScheduledCase;
import uk.gov.hmcts.darts.utilities.TestUtils;
import uk.gov.hmcts.darts.utilities.deserializer.LocalDateTimeTypeDeserializer;
import uk.gov.hmcts.darts.utilities.deserializer.LocalDateTypeDeserializer;
import uk.gov.hmcts.darts.utilities.deserializer.OffsetDateTimeTypeDeserializer;
import uk.gov.hmcts.darts.utilities.serializer.LocalDateTimeTypeSerializer;
import uk.gov.hmcts.darts.utilities.serializer.LocalDateTypeSerializer;
import uk.gov.hmcts.darts.utilities.serializer.OffsetDateTimeTypeSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class GetCasesMapperTest {

    @Test
    void mapOk() throws IOException, JSONException {
        GetCases getCasesRequest = new GetCases();
        getCasesRequest.setCourthouse("Swansea");
        getCasesRequest.setCourtroom("1");
        getCasesRequest.setDate("2023-06-20");
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "tests/cases/GetCasesMapperTest/mapOk/dartsApiResponse.json");

        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeTypeSerializer())
            .addSerializer(LocalDate.class, new LocalDateTypeSerializer())
            .addSerializer(OffsetDateTime.class, new OffsetDateTimeTypeSerializer())
            .addDeserializer(LocalDateTime.class, new LocalDateTimeTypeDeserializer())
            .addDeserializer(LocalDate.class, new LocalDateTypeDeserializer())
            .addDeserializer(OffsetDateTime.class, new OffsetDateTimeTypeDeserializer());
        ObjectMapper mapper =  new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerModule(module);
        List<ScheduledCase> modernisedDartsResponse = mapper.readValue(dartsApiResponseStr, new TypeReference<ArrayList<ScheduledCase>>() {});

        GetCasesResponse getCasesResponse = GetCasesMapper.mapToDfsResponse(
            getCasesRequest,
            modernisedDartsResponse
        );

        String actualResponse = mapper.writeValueAsString(getCasesResponse);

        String expectedResponse = TestUtils.getContentsFromFile(
            "tests/cases/GetCasesMapperTest/mapOk/expectedResponse.json");
        JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);
    }
}

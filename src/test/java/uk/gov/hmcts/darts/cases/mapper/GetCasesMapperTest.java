package uk.gov.hmcts.darts.cases.mapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.service.mojdarts.synapps.com.GetCases;
import com.service.mojdarts.synapps.com.GetCasesResponse;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import uk.gov.hmcts.darts.model.cases.ScheduledCase;
import uk.gov.hmcts.darts.utilities.LocalDateTimeTypeAdapter;
import uk.gov.hmcts.darts.utilities.LocalDateTypeAdapter;
import uk.gov.hmcts.darts.utilities.TestUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

        Type scheduledCaseListClassType = new TypeToken<ArrayList<ScheduledCase>>() { }.getType();


        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .create();
        List<ScheduledCase> modernisedDartsResponse = gson.fromJson(dartsApiResponseStr, scheduledCaseListClassType);

        GetCasesResponse getCasesResponse = GetCasesMapper.mapToMojDartsResponse(
            getCasesRequest,
            modernisedDartsResponse
        );

        String actualResponse = gson.toJson(getCasesResponse);

        String expectedResponse = TestUtils.getContentsFromFile(
            "tests/cases/GetCasesMapperTest/mapOk/expectedResponse.json");
        JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);
    }

}

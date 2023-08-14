package uk.gov.hmcts.darts.dailylist.mapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.courtservice.schemas.courtservice.DailyListStructure;
import uk.gov.hmcts.darts.model.dailyList.DailyList;
import uk.gov.hmcts.darts.utilities.LocalDateTimeTypeAdapter;
import uk.gov.hmcts.darts.utilities.LocalDateTypeAdapter;
import uk.gov.hmcts.darts.utilities.OffsetDateTimeTypeAdapter;
import uk.gov.hmcts.darts.utilities.TestUtils;
import uk.gov.hmcts.darts.utilities.XmlParser;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@SpringBootTest
class DailyListRequestMapperTest {

    final XmlParser xmlParser = new XmlParser();

    @Autowired
    DailyListRequestMapper dailyListRequestMapper;

    @Test
    void test1() throws IOException {
        String requestXML = TestUtils.getContentsFromFile(
            "tests/dailylist/DailyListRequestMapperTest/test1/request.xml");
        DailyListStructure legacyDailyList = xmlParser.unmarshal(requestXML, DailyListStructure.class);
        DailyList modernisedDailyList = dailyListRequestMapper.mapToEntity(legacyDailyList);

        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeTypeAdapter())
            .create();
        String json = gson.toJson(modernisedDailyList);

    }

}

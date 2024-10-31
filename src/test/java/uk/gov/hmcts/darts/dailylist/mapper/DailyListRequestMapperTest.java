package uk.gov.hmcts.darts.dailylist.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.courtservice.schemas.courtservice.DailyListStructure;
import uk.gov.hmcts.darts.model.dailylist.DailyListJsonObject;
import uk.gov.hmcts.darts.utilities.TestUtils;
import uk.gov.hmcts.darts.utilities.XmlParser;
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

@SpringBootTest
@SuppressWarnings({"PMD.UnitTestShouldIncludeAssert"})
class DailyListRequestMapperTest {

    final XmlParser xmlParser = new XmlParser();

    @Autowired
    DailyListRequestMapper dailyListRequestMapper;

    @Test
    void test1() throws IOException, JSONException {
        String requestXml = TestUtils.getContentsFromFile(
            "tests/dailylist/DailyListRequestMapperTest/test1/request.xml");
        DailyListStructure legacyDailyList = xmlParser.unmarshal(requestXml, DailyListStructure.class);
        DailyListJsonObject modernisedDailyList = dailyListRequestMapper.mapToEntity(legacyDailyList);

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

        String actualResponse = mapper.writeValueAsString(modernisedDailyList);
        String expectedResponse = TestUtils.getContentsFromFile(
            "tests/dailylist/DailyListRequestMapperTest/test1/expectedResponse.json");

        TestUtils.compareJson(expectedResponse, actualResponse);
    }

    @Test
    void test2() throws IOException, JSONException {
        String requestXml = TestUtils.getContentsFromFile(
            "tests/dailylist/DailyListRequestMapperTest/test2/request.xml");
        DailyListStructure legacyDailyList = xmlParser.unmarshal(requestXml, DailyListStructure.class);
        DailyListJsonObject modernisedDailyList = dailyListRequestMapper.mapToEntity(legacyDailyList);

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

        String actualResponse = mapper.writeValueAsString(modernisedDailyList);
        String expectedResponse = TestUtils.getContentsFromFile(
            "tests/dailylist/DailyListRequestMapperTest/test2/expectedResponse.json");

        TestUtils.compareJson(expectedResponse, actualResponse);
    }

}
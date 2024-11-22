package uk.gov.hmcts.darts.dailylist.mapper;

import org.junit.jupiter.api.Test;
import uk.gov.courtservice.schemas.courtservice.DailyListStructure;
import uk.gov.hmcts.darts.model.dailylist.PostDailyListRequest;
import uk.gov.hmcts.darts.utilities.TestUtils;
import uk.gov.hmcts.darts.utilities.XmlParser;

import java.io.IOException;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DailyListXmlRequestMapperTest {

    @Test
    void testPublishedTimeBstWithTimeZone() throws IOException {
        String requestXml = TestUtils.getContentsFromFile(
            "tests/dailylist/DailyListXmlRequestMapperTest/publishedDateBstWithTimeZone.xml");
        DailyListStructure dailyList = XmlParser.unmarshal(requestXml, DailyListStructure.class);
        PostDailyListRequest postDailyListRequest = DailyListXmlRequestMapper.mapToPostDailyListRequest(dailyList, requestXml, "XHB", "12345");

        OffsetDateTime publishedTime = postDailyListRequest.getPublishedTs();
        assertEquals("2024-04-23T16:00+01:00", publishedTime.toString());
    }

    @Test
    void testPublishedTimeBstWithoutTimeZone() throws IOException {
        String requestXml = TestUtils.getContentsFromFile(
            "tests/dailylist/DailyListXmlRequestMapperTest/publishedDateBstWithoutTimeZone.xml");
        DailyListStructure dailyList = XmlParser.unmarshal(requestXml, DailyListStructure.class);
        PostDailyListRequest postDailyListRequest = DailyListXmlRequestMapper.mapToPostDailyListRequest(dailyList, requestXml, "XHB", "12345");

        OffsetDateTime publishedTime = postDailyListRequest.getPublishedTs();
        assertEquals("2024-04-23T15:00+01:00", publishedTime.toString());
    }

    @Test
    void testPublishedTimeGmtWithTimeZone() throws IOException {
        String requestXml = TestUtils.getContentsFromFile(
            "tests/dailylist/DailyListXmlRequestMapperTest/publishedDateGmtWithTimeZone.xml");
        DailyListStructure dailyList = XmlParser.unmarshal(requestXml, DailyListStructure.class);
        PostDailyListRequest postDailyListRequest = DailyListXmlRequestMapper.mapToPostDailyListRequest(dailyList, requestXml, "XHB", "12345");

        OffsetDateTime publishedTime = postDailyListRequest.getPublishedTs();
        assertEquals("2024-01-12T11:00Z", publishedTime.toString());
    }

    @Test
    void testPublishedTimeGmtWithoutTimeZone() throws IOException {
        String requestXml = TestUtils.getContentsFromFile(
            "tests/dailylist/DailyListXmlRequestMapperTest/publishedDateGmtWithoutTimeZone.xml");
        DailyListStructure dailyList = XmlParser.unmarshal(requestXml, DailyListStructure.class);
        PostDailyListRequest postDailyListRequest = DailyListXmlRequestMapper.mapToPostDailyListRequest(dailyList, requestXml, "XHB", "12345");

        OffsetDateTime publishedTime = postDailyListRequest.getPublishedTs();
        assertEquals("2024-01-12T11:00Z", publishedTime.toString());
    }
}

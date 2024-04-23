package uk.gov.hmcts.darts.dailylist.mapper;

import org.junit.jupiter.api.Test;
import uk.gov.courtservice.schemas.courtservice.DailyListStructure;
import uk.gov.hmcts.darts.dailylist.model.PostDailyListRequest;
import uk.gov.hmcts.darts.utilities.TestUtils;
import uk.gov.hmcts.darts.utilities.XmlParser;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DailyListXmlRequestMapperTest {

    private final XmlParser xmlParser = new XmlParser();

    @Test
    void testPublishedTimeBstWithTimeZone() throws IOException {
        String requestXml = TestUtils.getContentsFromFile(
            "tests/dailylist/DailyListXmlRequestMapperTest/publishedDateBstWithTimeZone.xml");
        DailyListStructure dailyList = xmlParser.unmarshal(requestXml, DailyListStructure.class);
        PostDailyListRequest postDailyListRequest = DailyListXmlRequestMapper.mapToPostDailyListRequest(dailyList, requestXml);

        String publishedTime = postDailyListRequest.getPublishedTs();
        assertEquals("2024-04-23T16:00+01:00", publishedTime);
    }

    @Test
    void testPublishedTimeBstWithoutTimeZone() throws IOException {
        String requestXml = TestUtils.getContentsFromFile(
            "tests/dailylist/DailyListXmlRequestMapperTest/publishedDateBstWithoutTimeZone.xml");
        DailyListStructure dailyList = xmlParser.unmarshal(requestXml, DailyListStructure.class);
        PostDailyListRequest postDailyListRequest = DailyListXmlRequestMapper.mapToPostDailyListRequest(dailyList, requestXml);

        String publishedTime = postDailyListRequest.getPublishedTs();
        assertEquals("2024-04-23T15:00+01:00", publishedTime);
    }

    @Test
    void testPublishedTimeGmtWithTimeZone() throws IOException {
        String requestXml = TestUtils.getContentsFromFile(
            "tests/dailylist/DailyListXmlRequestMapperTest/publishedDateGmtWithTimeZone.xml");
        DailyListStructure dailyList = xmlParser.unmarshal(requestXml, DailyListStructure.class);
        PostDailyListRequest postDailyListRequest = DailyListXmlRequestMapper.mapToPostDailyListRequest(dailyList, requestXml);

        String publishedTime = postDailyListRequest.getPublishedTs();
        assertEquals("2024-01-12T11:00Z", publishedTime);
    }

    @Test
    void testPublishedTimeGmtWithoutTimeZone() throws IOException {
        String requestXml = TestUtils.getContentsFromFile(
            "tests/dailylist/DailyListXmlRequestMapperTest/publishedDateGmtWithoutTimeZone.xml");
        DailyListStructure dailyList = xmlParser.unmarshal(requestXml, DailyListStructure.class);
        PostDailyListRequest postDailyListRequest = DailyListXmlRequestMapper.mapToPostDailyListRequest(dailyList, requestXml);

        String publishedTime = postDailyListRequest.getPublishedTs();
        assertEquals("2024-01-12T11:00Z", publishedTime);
    }
}

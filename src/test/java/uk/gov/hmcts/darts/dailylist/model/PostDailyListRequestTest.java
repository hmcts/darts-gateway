package uk.gov.hmcts.darts.dailylist.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.utilities.TestUtils;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class PostDailyListRequestTest {


    private String dailyListXmlWithLineBreaks;

    @BeforeEach
    void setUp() throws IOException {
        dailyListXmlWithLineBreaks = TestUtils.getContentsFromFile(
            "tests/dailylist/DailyListRequestMapperTest/valid-dailyList-with-line-breaks.xml");
    }

    @Test
    void removesLineBreaksFromDailyList() {
        var request = new PostDailyListRequest();
        request.setDailyListXml(dailyListXmlWithLineBreaks);

        assertThat(request.getDailyListXml()).doesNotContain("\n", "\\n", "\r", "\\r");
    }
}

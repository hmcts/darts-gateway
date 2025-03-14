package uk.gov.hmcts.darts.document;

import org.junit.jupiter.api.Test;
import uk.gov.courtservice.events.DartsEvent;
import uk.gov.hmcts.darts.common.exceptions.DartsException;
import uk.gov.hmcts.darts.utilities.XmlParser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class XmlParserTest {

    private static String eventXML = """
        <be:DartsEvent xmlns:be=\"urn:integration-cjsonline-gov-uk:pilot:entities\"
                ID=\"0\"
                Y=\"2019\"
                M=\"10\"
                D=\"24\"
                H=\"15\"
                MIN=\"52\"
                S=\"41\">
            <be:CourtHouse>SNARESBROOK</be:CourtHouse>
            <be:CourtRoom>7</be:CourtRoom>
            <be:CaseNumbers>
                <be:CaseNumber>T20190441</be:CaseNumber>
            </be:CaseNumbers>
            <be:EventText>test</be:EventText>
        </be:DartsEvent>""";

    private static String notParsableXML = """
        <be:xxx xmlns:be=\"urn:integration-cjsonline-gov-uk:pilot:entities\"
                ID=\"0\"
                Y=\"2019\"
                M=\"10\"
                D=\"24\"
                H=\"15\"
                MIN=\"52\"
                S=\"41\">
            <be:CourtHouse>SNARESBROOK</be:CourtHouse>
            <be:CourtRoom>7</be:CourtRoom>
            <be:CaseNumbers>
                <be:CaseNumber>T20190441</be:CaseNumber>
            </be:CaseNumbers>
            <be:EventText>test</be:EventText>
        </be:xxx>""";

    @Test
    void unmarshalEvent() {
        DartsEvent dartsEvent = XmlParser.unmarshal(eventXML, DartsEvent.class);

        assertThat(dartsEvent.getID()).isEqualTo(0);
        assertThat(dartsEvent.getY()).isEqualTo(2019);
        assertThat(dartsEvent.getM()).isEqualTo(10);
        assertThat(dartsEvent.getD()).isEqualTo(24);
        assertThat(dartsEvent.getH()).isEqualTo(15);
        assertThat(dartsEvent.getMIN()).isEqualTo(52);
        assertThat(dartsEvent.getS()).isEqualTo(41);
        assertThat(dartsEvent.getCourtHouse()).isEqualTo("SNARESBROOK");
        assertThat(dartsEvent.getCourtRoom()).isEqualTo("7");
        assertThat(dartsEvent.getCaseNumbers().getCaseNumber()).containsExactly("T20190441");
    }

    @Test
    void throwsWhenXmlNotParsable() {
        assertThatThrownBy(() -> XmlParser.unmarshal(notParsableXML, DartsEvent.class))
            .isInstanceOf(DartsException.class);
    }
}

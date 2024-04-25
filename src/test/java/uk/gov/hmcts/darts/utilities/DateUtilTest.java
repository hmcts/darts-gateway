package uk.gov.hmcts.darts.utilities;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.darts.utilities.DateUtil.LONDON_ZONE_ID;
import static uk.gov.hmcts.darts.utilities.DateUtil.UTC;

@SuppressWarnings("PMD.LineLength")
class DateUtilTest {

    @Test
    void gmt() throws DatatypeConfigurationException {
        GregorianCalendar gcal = new GregorianCalendar(2020, Calendar.MARCH, 21, 15, 23, 39);
        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        OffsetDateTime offsetDateTime = DateUtil.toOffsetDateTime(xcal);
        assertEquals("2020-03-21T15:23:39Z", offsetDateTime.toString());
    }

    @Test
    void gmtWithTimeZone() throws DatatypeConfigurationException {
        GregorianCalendar gcal = new GregorianCalendar(2020, Calendar.MARCH, 21, 15, 23, 39);
        gcal.setTimeZone(TimeZone.getTimeZone(UTC));
        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        OffsetDateTime offsetDateTime = DateUtil.toOffsetDateTime(xcal);
        assertEquals("2020-03-21T15:23:39Z", offsetDateTime.toString());
    }

    @Test
    void bst() throws DatatypeConfigurationException {
        GregorianCalendar gcal = new GregorianCalendar(2020, Calendar.JULY, 21, 15, 23, 39);
        gcal.setTimeZone(TimeZone.getTimeZone(LONDON_ZONE_ID));
        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        OffsetDateTime offsetDateTime = DateUtil.toOffsetDateTime(xcal);
        assertEquals("2020-07-21T15:23:39+01:00", offsetDateTime.toString());
    }

    @Test
    void bstWithTimeZone() throws DatatypeConfigurationException {
        GregorianCalendar gcal = new GregorianCalendar(2024, Calendar.APRIL, 23, 15, 0, 0);
        gcal.setTimeZone(TimeZone.getTimeZone(UTC));
        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        OffsetDateTime offsetDateTime = DateUtil.toOffsetDateTime(xcal);
        assertEquals("2024-04-23T16:00+01:00", offsetDateTime.toString());
    }
}

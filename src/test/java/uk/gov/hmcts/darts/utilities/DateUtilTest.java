package uk.gov.hmcts.darts.utilities;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateUtilTest {

    @Test
    void gmt() throws DatatypeConfigurationException {
        GregorianCalendar gcal = new GregorianCalendar(2020, 2, 21, 15, 23, 39);
        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        OffsetDateTime offsetDateTime = DateUtil.toOffsetDateTime(xcal);
        assertEquals("2020-03-21T15:23:39Z", offsetDateTime.toString());
    }

    @Test
    void bst() throws DatatypeConfigurationException {
        GregorianCalendar gcal = new GregorianCalendar(2020, 6, 21, 15, 23, 39);
        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        OffsetDateTime offsetDateTime = DateUtil.toOffsetDateTime(xcal);
        assertEquals("2020-07-21T15:23:39+01:00", offsetDateTime.toString());
    }

    @Test
    void gmt2() throws DatatypeConfigurationException {
        GregorianCalendar gcal = new GregorianCalendar(2020, 2, 21, 15, 23, 39);
        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        OffsetDateTime offsetDateTime = DateUtil.toOffsetDateTime2(xcal);
        assertEquals("2020-03-21T15:23:39Z", offsetDateTime.toString());
    }

    @Test
    void bst2() throws DatatypeConfigurationException {
        GregorianCalendar gcal = new GregorianCalendar(2020, 6, 21, 15, 23, 39);
        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        OffsetDateTime offsetDateTime = DateUtil.toOffsetDateTime2(xcal);
        assertEquals("2020-07-21T15:23:39+01:00", offsetDateTime.toString());
    }

    @Test
    void gmt3() throws DatatypeConfigurationException {
        GregorianCalendar gcal = new GregorianCalendar(2020, 2, 21, 15, 23, 39);
        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        OffsetDateTime offsetDateTime = DateUtil.toOffsetDateTime3(xcal);
        assertEquals("2020-03-21T15:23:39Z", offsetDateTime.toString());
    }

    @Test
    void bst3() throws DatatypeConfigurationException {
        GregorianCalendar gcal = new GregorianCalendar(2020, 6, 21, 15, 23, 39);
        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        OffsetDateTime offsetDateTime = DateUtil.toOffsetDateTime3(xcal);
        assertEquals("2020-07-21T14:23:39Z", offsetDateTime.toString());
    }

    @Test
    void test1() throws DatatypeConfigurationException {
        GregorianCalendar gcal = new GregorianCalendar(2020, 6, 21, 15, 23, 39);
        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        GregorianCalendar gregorianCalendar = xcal.toGregorianCalendar();
        ZonedDateTime zonedDateTime = gregorianCalendar.toZonedDateTime();
        Instant instant = zonedDateTime.toInstant();
        String result = instant.atOffset(ZoneOffset.UTC).toString();
        assertEquals("2020-07-21T14:23:39Z", result);
    }

    @Test
    void test2() throws DatatypeConfigurationException {
        GregorianCalendar gcal = new GregorianCalendar(2020, 6, 21, 15, 23, 39);
        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        GregorianCalendar gregorianCalendar = xcal.toGregorianCalendar();
        String result = gregorianCalendar.toString();
        assertEquals("java.util.GregorianCalendar[time=?,areFieldsSet=false,areAllFieldsSet=true,lenient=true,zone=sun.util.calendar.ZoneInfo[id=\"GMT+01:00\",offset=3600000,dstSavings=0,useDaylight=false,transitions=0,lastRule=null],firstDayOfWeek=2,minimalDaysInFirstWeek=4,ERA=1,YEAR=2020,MONTH=6,WEEK_OF_YEAR=1,WEEK_OF_MONTH=1,DAY_OF_MONTH=21,DAY_OF_YEAR=1,DAY_OF_WEEK=5,DAY_OF_WEEK_IN_MONTH=1,AM_PM=0,HOUR=0,HOUR_OF_DAY=15,MINUTE=23,SECOND=39,MILLISECOND=0,ZONE_OFFSET=3600000,DST_OFFSET=0]", result);
    }

    @Test
    void test3() throws DatatypeConfigurationException {
        GregorianCalendar gcal = new GregorianCalendar(2020, 6, 21, 15, 23, 39);
        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        String result = xcal.toString();
        assertEquals("2020-07-21T15:23:39.000+01:00", result);
    }



}

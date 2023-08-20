package uk.gov.hmcts.darts.utilities;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateUtilTest {

    @Test
    public void GMT() throws DatatypeConfigurationException {
        GregorianCalendar gcal = new GregorianCalendar(2020, 2, 21, 15, 23, 39);
        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        OffsetDateTime offsetDateTime = DateUtil.toOffsetDateTime(xcal);
        assertEquals("2020-03-21T15:23:39Z", offsetDateTime.toString());
    }

    @Test
    public void BST() throws DatatypeConfigurationException {
        GregorianCalendar gcal = new GregorianCalendar(2020, 6, 21, 15, 23, 39);
        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        OffsetDateTime offsetDateTime = DateUtil.toOffsetDateTime(xcal);
        assertEquals("2020-07-21T15:23:39+01:00", offsetDateTime.toString());
    }

    @Test
    public void GMT2() throws DatatypeConfigurationException {
        GregorianCalendar gcal = new GregorianCalendar(2020, 2, 21, 15, 23, 39);
        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        OffsetDateTime offsetDateTime = DateUtil.toOffsetDateTime2(xcal);
        assertEquals("2020-03-21T15:23:39Z", offsetDateTime.toString());
    }

    @Test
    public void BST2() throws DatatypeConfigurationException {
        GregorianCalendar gcal = new GregorianCalendar(2020, 6, 21, 15, 23, 39);
        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        OffsetDateTime offsetDateTime = DateUtil.toOffsetDateTime2(xcal);
        assertEquals("2020-07-21T15:23:39+01:00", offsetDateTime.toString());
    }

}

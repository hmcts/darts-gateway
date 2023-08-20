package uk.gov.hmcts.darts.utilities;

import lombok.experimental.UtilityClass;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import javax.xml.datatype.XMLGregorianCalendar;

@UtilityClass
public class DateUtil {
    public OffsetDateTime toOffsetDateTime(XMLGregorianCalendar date) {
        GregorianCalendar gregorianCalendar = date.toGregorianCalendar();
        ZonedDateTime zonedDateTime = gregorianCalendar.toZonedDateTime();
        return zonedDateTime.toOffsetDateTime();

    }
}

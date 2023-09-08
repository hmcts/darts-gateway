package uk.gov.hmcts.darts.dailylist.mapper;

import lombok.experimental.UtilityClass;
import uk.gov.courtservice.schemas.courtservice.DailyListStructure;
import uk.gov.hmcts.darts.dailylist.model.PostDailyListRequest;
import uk.gov.hmcts.darts.utilities.DateUtil;

import javax.xml.datatype.XMLGregorianCalendar;

@UtilityClass
public class DailyListXmlRequestMapper {
    public PostDailyListRequest mapToPostDailyListRequest(DailyListStructure legacyDailyListObject, String document) {
        PostDailyListRequest request = new PostDailyListRequest();
        request.setCourthouse(legacyDailyListObject.getCrownCourt().getCourtHouseName());
        request.setUniqueId(legacyDailyListObject.getDocumentID().getUniqueID());
        XMLGregorianCalendar publishedTime = legacyDailyListObject.getListHeader().getPublishedTime();
        request.setPublishedTs(DateUtil.toOffsetDateTime(publishedTime).toString());
        request.setHearingDate(legacyDailyListObject
                                   .getCourtLists().getCourtList().get(0)
                                   .getSittings().getSitting().get(0)
                                   .getHearings().getHearing().get(0)
                                   .getHearingDetails().getHearingDate().toString());
        request.setDailyListXml(document);
        return request;
    }
}

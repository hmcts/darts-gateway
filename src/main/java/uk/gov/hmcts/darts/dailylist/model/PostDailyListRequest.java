package uk.gov.hmcts.darts.dailylist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostDailyListRequest {

    String courthouse;
    String hearingDate;
    String uniqueId;
    String publishedTs;
    String dailyListXml;
    String dailyListJson;

    public void setDailyListXml(String dailyListXml) {
        this.dailyListXml = dailyListXml.replaceAll("\\r\\n|\\r|\\n", "");
    }
}

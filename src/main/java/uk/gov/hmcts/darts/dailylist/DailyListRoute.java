package uk.gov.hmcts.darts.dailylist;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.utilities.XmlParser;

@Service
@AllArgsConstructor
public class DailyListRoute {

    private final XmlParser xmlParser;

    public void route(String document, String messageId) {
        // Currently not implemented due to missing XSD
    }
}

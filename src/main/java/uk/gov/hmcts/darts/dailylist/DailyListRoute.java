package uk.gov.hmcts.darts.dailylist;

import com.service.mojdarts.synapps.com.AddDocumentResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.utilities.XmlParser;

@Service
@AllArgsConstructor
public class DailyListRoute {

    private final XmlParser xmlParser;

    public AddDocumentResponse handle(String document, String messageId) {
        // Currently not implemented. Included to establish the routing pattern
        // as it arrives at the same SOAP endpoint as Events.
        return new AddDocumentResponse();
    }
}

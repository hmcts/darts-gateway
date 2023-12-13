package uk.gov.hmcts.darts.common.client.exeption;

import org.springframework.web.client.HttpStatusCodeException;
import uk.gov.hmcts.darts.ws.DartsException;

public interface DartsClientProblemDecoder {
    DartsException decode(HttpStatusCodeException response);
}

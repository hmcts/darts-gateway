package uk.gov.hmcts.darts.common.client1.exeption;

import org.springframework.web.client.HttpStatusCodeException;
import uk.gov.hmcts.darts.common.exceptions.DartsException;

public interface DartsClientProblemDecoder {
    DartsException decode(HttpStatusCodeException response);
}

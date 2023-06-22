package uk.gov.hmcts.darts.events.service;

import uk.gov.hmcts.darts.events.model.DarNotifyEvent;

public interface DarNotifyEventService {

    void darNotify(DarNotifyEvent darNotifyEvent);

}

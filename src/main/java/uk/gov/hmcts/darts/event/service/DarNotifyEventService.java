package uk.gov.hmcts.darts.event.service;

import uk.gov.hmcts.darts.event.model.DarNotifyEvent;

@FunctionalInterface
public interface DarNotifyEventService {
    void darNotify(DarNotifyEvent darNotifyEvent);
}

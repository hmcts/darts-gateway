package uk.gov.hmcts.darts.event.service;

import uk.gov.hmcts.dartsgateway.notification.model.NotificationDetails;

public interface DarNotifyEventService {

    void darNotify(NotificationDetails darNotifyEvent);

}

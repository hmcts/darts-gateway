package uk.gov.hmcts.darts.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.darts.event.service.DarNotifyEventService;
import uk.gov.hmcts.dartsgateway.notification.api.NotifyDarApi;
import uk.gov.hmcts.dartsgateway.notification.model.NotificationDetails;

@RequiredArgsConstructor
@RestController
public class DarNotifyController implements NotifyDarApi {

    private final DarNotifyEventService darNotifyEventService;

    @Override
    public ResponseEntity<Void> notifyDAR(@RequestBody NotificationDetails darNotifyEvent) {
        darNotifyEventService.darNotify(darNotifyEvent);
        return ResponseEntity.ok().build();
    }

}

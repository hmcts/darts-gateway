package uk.gov.hmcts.darts.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.darts.events.model.DarNotifyEvent;
import uk.gov.hmcts.darts.events.service.DarNotifyEventService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class DarNotifyController {

    private final DarNotifyEventService darNotifyEventService;

    @PostMapping(
        value = "/events/dar-notify",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> darNotify(@RequestBody DarNotifyEvent darNotifyEvent) {
        log.info("Received {}", darNotifyEvent);
        darNotifyEventService.darNotify(darNotifyEvent);

        return ResponseEntity.ok().build();
    }

}

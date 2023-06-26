package uk.gov.hmcts.darts.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.darts.event.model.DarNotifyEvent;
import uk.gov.hmcts.darts.event.service.DarNotifyEventService;

@RequiredArgsConstructor
@RestController
public class DarNotifyController {

    private final DarNotifyEventService darNotifyEventService;

    @PostMapping(
        value = "/events/dar-notify",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> darNotify(@RequestBody DarNotifyEvent darNotifyEvent) {
        darNotifyEventService.darNotify(darNotifyEvent);
        return ResponseEntity.ok().build();
    }

}

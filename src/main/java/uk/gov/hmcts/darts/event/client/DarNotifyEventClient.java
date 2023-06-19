package uk.gov.hmcts.darts.event.client;

import com.viqsoultions.DARNotifyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;

@RequiredArgsConstructor
@Component
@Slf4j
public class DarNotifyEventClient {

    private final WebServiceTemplate webServiceTemplate;

    public void darNotifyEvent(DARNotifyEvent darNotifyEvent) {
        Object responseObj = webServiceTemplate.marshalSendAndReceive(
            "http://localhost:8080/VIQDARNotifyEvent/DARNotifyEvent.asmx",
            darNotifyEvent,
            new SoapActionCallback("http://www.VIQSoultions.com/DARNotifyEvent")
        );
        log.debug((String) responseObj);

        DarNotifyEventResult result = null;
        if (responseObj instanceof Integer) {
            result = DarNotifyEventResult.valueOfResult((Integer) responseObj);
        } else if (responseObj instanceof String) {
            result = DarNotifyEventResult.valueOfResult(Integer.valueOf((String) responseObj));
        }

        if (DarNotifyEventResult.OK.equals(result)) {
            log.info("DAR Notify was successful");
        } else if (result != null) {
            log.warn("DAR Notify failed with message: {}", result.getMessage());
        } else {
            log.warn("DAR Notify failed with unknown result code");
        }
    }

}

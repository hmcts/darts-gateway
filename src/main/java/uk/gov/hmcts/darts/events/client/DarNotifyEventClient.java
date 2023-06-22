package uk.gov.hmcts.darts.events.client;

import com.viqsoultions.DARNotifyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.hmcts.darts.events.config.DarNotifyEventConfigurationProperties;

@Component
@Slf4j
@RequiredArgsConstructor
public class DarNotifyEventClient {

    private final DarNotifyEventConfigurationProperties darNotifyEventConfigurationProperties;
    private final WebServiceTemplate webServiceTemplate;

    public void darNotifyEvent(String darNotificationUrl, DARNotifyEvent darNotifyEvent) {
        Object responseObj = webServiceTemplate.marshalSendAndReceive(
            darNotificationUrl != null ? darNotificationUrl
                : darNotifyEventConfigurationProperties.getDefaultNotificationUrl().toExternalForm(),
            darNotifyEvent,
            new SoapActionCallback(darNotifyEventConfigurationProperties.getSoapAction().toExternalForm())
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

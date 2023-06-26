package uk.gov.hmcts.darts.event.client;

import com.viqsoultions.DARNotifyEvent;
import com.viqsoultions.DARNotifyEventResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.hmcts.darts.event.config.DarNotifyEventConfigurationProperties;
import uk.gov.hmcts.darts.event.enums.DarNotifyEventResult;

import static uk.gov.hmcts.darts.event.enums.DarNotifyEventResult.OK;

@Component
@Slf4j
@RequiredArgsConstructor
public class DarNotifyEventClient {

    private final DarNotifyEventConfigurationProperties darNotifyEventConfigurationProperties;
    private final WebServiceTemplate webServiceTemplate;

    // This SOAP Web Service operation (DARNotifyEvent) still needs to be fully integration tested
    public boolean darNotifyEvent(String darNotificationUrl, DARNotifyEvent request) {
        boolean successful = false;

        String uri = darNotificationUrl != null ? darNotificationUrl
            : darNotifyEventConfigurationProperties.getDefaultNotificationUrl().toExternalForm();

        String caseNumbers = request.getXMLEventDocument().getEvent().getCaseNumbers().getCaseNumber().toString();

        try {
            Object responseObj = webServiceTemplate.marshalSendAndReceive(
                uri,
                request,
                new SoapActionCallback(darNotifyEventConfigurationProperties.getSoapAction().toExternalForm())
            );

            if (responseObj instanceof DARNotifyEventResponse) {
                DARNotifyEventResponse response = (DARNotifyEventResponse) responseObj;
                DarNotifyEventResult result = DarNotifyEventResult.valueOfResult(response.getDARNotifyEventResult());

                if (OK.equals(result)) {
                    log.info("DAR Notify successfully sent to: {}, caseNumbers: {}",
                             uri, caseNumbers
                    );
                    successful = true;
                } else if (result != null) {
                    log.warn("DAR Notify to {} failed with message: {}, caseNumbers: {}",
                             uri, result.getMessage(), caseNumbers
                    );
                } else {
                    log.warn("DAR Notify to {} failed with unknown result code: {}, caseNumbers: {}",
                             uri, response.getDARNotifyEventResult(), caseNumbers
                    );
                }
            } else {
                log.warn("DAR Notify to {} did not respond, caseNumbers: {}",
                         uri, caseNumbers
                );
            }

        } catch (WebServiceException webServiceException) {
            log.error("DAR Notify to {} FAILED",
                      uri, webServiceException
            );
        }

        return successful;
    }

}

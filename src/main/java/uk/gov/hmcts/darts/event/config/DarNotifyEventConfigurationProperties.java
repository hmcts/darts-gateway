package uk.gov.hmcts.darts.event.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URL;

@ConfigurationProperties("darts-gateway.events.dar-notify-event")
@Validated
@Getter
@Setter
@ToString
public class DarNotifyEventConfigurationProperties {

    @NotNull
    private URL soapAction;

}

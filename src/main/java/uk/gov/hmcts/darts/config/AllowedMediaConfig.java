package uk.gov.hmcts.darts.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@ConfigurationProperties(prefix = "darts-gateway.add-audio")
@Getter
@Setter
@Validated
public class AllowedMediaConfig {
    private List<String> allowedMediaFormats;
}

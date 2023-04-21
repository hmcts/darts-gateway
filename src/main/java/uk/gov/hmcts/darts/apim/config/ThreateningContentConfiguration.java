package uk.gov.hmcts.darts.apim.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.darts.apim.validate.model.ThreateningContentRule;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "validate")
public class ThreateningContentConfiguration {

    @Getter
    private final List<ThreateningContentRule> threateningContentRules = new ArrayList<>();

}

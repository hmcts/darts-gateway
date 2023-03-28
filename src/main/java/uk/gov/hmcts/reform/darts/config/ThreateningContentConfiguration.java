package uk.gov.hmcts.reform.darts.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.darts.validate.model.ThreateningContentRule;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "validate")
public class ThreateningContentConfiguration {

    @Getter
    @Setter
    private List<ThreateningContentRule> threateningContentRules = new ArrayList<>();

}

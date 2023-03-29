package uk.gov.hmcts.reform.darts.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "validate.xml-complexity")
@Getter
@Setter
public class XmlComplexityConfiguration {

    private int maxNodes;

    private int maxLevelsDescendantNodes;

    private int maxChildNodesPerNode;

    private int maxAttributesPerNode;
}

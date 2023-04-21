package uk.gov.hmcts.darts.apim.validate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class XmlComplexityRule {

    private final String name;

    private final String xpath;
}

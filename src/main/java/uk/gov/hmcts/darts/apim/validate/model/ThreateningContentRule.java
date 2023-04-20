package uk.gov.hmcts.darts.apim.validate.model;

import lombok.Getter;

import java.util.regex.Pattern;

@Getter
public class ThreateningContentRule {

    private final String name;

    private final Pattern pattern;

    private final String message;

    /**
     * Constructor for class.
     *
     * @param name The name of the threatening content rule
     * @param pattern A string containing a regular expression that defines the rule
     * @param message The message associated with a failure of the rule
     */
    public ThreateningContentRule(String name, String pattern, String message) {
        this.name = name;
        this.pattern = Pattern.compile(pattern);
        this.message = message;
    }
}

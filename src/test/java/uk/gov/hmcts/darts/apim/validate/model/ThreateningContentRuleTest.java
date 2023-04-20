package uk.gov.hmcts.darts.apim.validate.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThreateningContentRuleTest {

    private static final String RULE_NAME = "testRuleName";
    private static final String RULE_PATTERN = ".*test.*";
    private static final String RULE_MESSAGE = "Test message";

    @Test
    void testGetters() {
        ThreateningContentRule testRule = new ThreateningContentRule(RULE_NAME, RULE_PATTERN, RULE_MESSAGE);

        assertEquals(RULE_NAME, testRule.getName(), "Name does not match expected name");
        assertEquals(RULE_PATTERN, testRule.getPattern().pattern(), "Pattern does not match expected pattern");
        assertEquals(RULE_MESSAGE, testRule.getMessage(), "Message does not match expected message");
    }
}

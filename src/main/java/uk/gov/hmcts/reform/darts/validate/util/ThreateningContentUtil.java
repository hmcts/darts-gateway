package uk.gov.hmcts.reform.darts.validate.util;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.darts.validate.model.ThreateningContentRule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ThreateningContentUtil {

    /**
     * Check if some text contains threatening content.
     * <p/>
     * The text is deemed to contain threatening content it matches the regular expression defined for the rule.
     *
     * @param text The text to check
     * @param rule The rule to use to check the text
     * @return True if the text contains threatening content as defined by the rule
     */
    public boolean containsThreateningContent(String text, ThreateningContentRule rule) {
        Pattern pattern = rule.getPattern();
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }
}

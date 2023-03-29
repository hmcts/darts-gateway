package uk.gov.hmcts.reform.darts.validate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.darts.config.ThreateningContentConfiguration;
import uk.gov.hmcts.reform.darts.validate.domain.ValidateResult;
import uk.gov.hmcts.reform.darts.validate.model.ThreateningContentRule;
import uk.gov.hmcts.reform.darts.validate.util.ThreateningContentUtil;

import java.util.ListIterator;

@Component
@Slf4j
public class ThreateningContentService implements ValidateService {

    private final ThreateningContentConfiguration threateningContentConfiguration;

    private final ThreateningContentUtil threateningContentUtil;

    @Autowired
    public ThreateningContentService(ThreateningContentConfiguration threateningContentConfiguration,
                                     ThreateningContentUtil threateningContentUtil) {
        this.threateningContentConfiguration = threateningContentConfiguration;
        this.threateningContentUtil = threateningContentUtil;
    }

    @Override
    public ValidateResult validateContent(String content) {
        boolean requestValid = true;
        String message = "";

        ListIterator<ThreateningContentRule> iterator =
            threateningContentConfiguration.getThreateningContentRules().listIterator();
        ThreateningContentRule currentRule;

        while (iterator.hasNext() && requestValid) {
            currentRule = iterator.next();
            if (threateningContentUtil.containsThreateningContent(content, currentRule)) {
                log.error("Threatening content detected: {}", currentRule.getName());
                requestValid = false;
                message = currentRule.getMessage();
            }
        }

        return new ValidateResult(requestValid, message);
    }
}

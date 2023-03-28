package uk.gov.hmcts.reform.darts.validate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import uk.gov.hmcts.reform.darts.config.XmlComplexityConfiguration;
import uk.gov.hmcts.reform.darts.validate.domain.ValidateResult;
import uk.gov.hmcts.reform.darts.validate.helper.XmlComplexityServiceHelper;
import uk.gov.hmcts.reform.darts.validate.model.XmlComplexityRule;
import uk.gov.hmcts.reform.darts.validate.util.XmlComplexityUtil;

import java.util.List;
import java.util.ListIterator;

@Component
@Slf4j
public class XmlComplexityService implements ValidateService {

    private final List<XmlComplexityRule> xmlComplexityRules;

    private final XmlComplexityServiceHelper xmlComplexityServiceHelper;

    private final XmlComplexityUtil xmlComplexityUtil;

    @Autowired
    public XmlComplexityService(XmlComplexityConfiguration xmlComplexityConfiguration,
                                XmlComplexityServiceHelper xmlComplexityServiceHelper,
                                XmlComplexityUtil xmlComplexityUtil) {
        this.xmlComplexityServiceHelper = xmlComplexityServiceHelper;
        xmlComplexityRules = this.xmlComplexityServiceHelper.getXmlComplexityRules(xmlComplexityConfiguration);
        this.xmlComplexityUtil = xmlComplexityUtil;
    }

    @Override
    public ValidateResult validateContent(String content) {
        boolean contentValid = true;
        String message = "";

        Document doc = xmlComplexityServiceHelper.convertTextToXml(content);

        ListIterator<XmlComplexityRule> iterator = xmlComplexityRules.listIterator();
        XmlComplexityRule currentRule;

        while (iterator.hasNext() && contentValid) {
            currentRule = iterator.next();
            if (xmlComplexityUtil.isComplexityRuleBroken(doc, currentRule)) {
                log.error("XML complexity rule broken: {}", currentRule.getName());
                contentValid = false;
                message = "Content fails XML complexity check";
            }
        }

        return new ValidateResult(contentValid, message);
    }
}

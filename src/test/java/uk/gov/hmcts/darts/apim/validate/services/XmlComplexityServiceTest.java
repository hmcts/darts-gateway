package uk.gov.hmcts.darts.apim.validate.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;
import uk.gov.hmcts.darts.apim.config.XmlComplexityConfiguration;
import uk.gov.hmcts.darts.apim.validate.domain.ValidateResult;
import uk.gov.hmcts.darts.apim.validate.helper.XmlComplexityServiceHelper;
import uk.gov.hmcts.darts.apim.validate.model.XmlComplexityRule;
import uk.gov.hmcts.darts.apim.validate.util.XmlComplexityUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class XmlComplexityServiceTest {

    @Mock
    private XmlComplexityConfiguration mockXmlComplexityConfiguration;

    @Mock
    private XmlComplexityServiceHelper mockXmlComplexityServiceHelper;

    @Mock
    private XmlComplexityUtil mockXmlComplexityUtil;

    @Mock
    private Document mockDocument;

    private List<XmlComplexityRule> complexityRules;

    /** Class being tested. */
    private XmlComplexityService xmlComplexityService;

    @Nested
    class ComplexityRulesPresentTest {

        @BeforeEach
        void setUp() {
            complexityRules = new ArrayList<>();
            XmlComplexityRule maxNodesRule =
                new XmlComplexityRule("maxNodes", "count(//*) > 2");
            complexityRules.add(maxNodesRule);
            XmlComplexityRule maxAttributesRule =
                new XmlComplexityRule("maxAttributes", "count(//*[count(@*) > 1]) > 0");
            complexityRules.add(maxAttributesRule);

            when(mockXmlComplexityServiceHelper.getXmlComplexityRules(mockXmlComplexityConfiguration))
                .thenReturn(complexityRules);

            xmlComplexityService = new XmlComplexityService(mockXmlComplexityConfiguration,
                                                            mockXmlComplexityServiceHelper,
                                                            mockXmlComplexityUtil);
        }

        @Test
        void testContentValid() {
            String content = "<root><child1/></root>";

            when(mockXmlComplexityServiceHelper.convertTextToXml(content)).thenReturn(mockDocument);

            final XmlComplexityRule maxNodesRule = complexityRules.get(0);
            final XmlComplexityRule maxAttributesRule = complexityRules.get(1);

            when(mockXmlComplexityUtil.isComplexityRuleBroken(mockDocument, maxNodesRule)).thenReturn(false);
            when(mockXmlComplexityUtil.isComplexityRuleBroken(mockDocument, maxAttributesRule)).thenReturn(false);

            ValidateResult result = xmlComplexityService.validateContent(content);

            assertTrue(result.isValid(), "Validation should indicate that content is valid");
            assertEquals("", result.getError(), "No error message expected");

            verify(mockXmlComplexityServiceHelper).getXmlComplexityRules(mockXmlComplexityConfiguration);
            verify(mockXmlComplexityServiceHelper).convertTextToXml(content);
            verify(mockXmlComplexityUtil).isComplexityRuleBroken(mockDocument, maxNodesRule);
            verify(mockXmlComplexityUtil).isComplexityRuleBroken(mockDocument, maxAttributesRule);
        }

        @Test
        void testContentInvalid() {
            String content = "<root><child1/><child2/></root>";

            when(mockXmlComplexityServiceHelper.convertTextToXml(content)).thenReturn(mockDocument);

            final XmlComplexityRule maxNodesRule = complexityRules.get(0);
            final XmlComplexityRule maxAttributesRule = complexityRules.get(1);

            when(mockXmlComplexityUtil.isComplexityRuleBroken(mockDocument, maxNodesRule)).thenReturn(true);

            ValidateResult result = xmlComplexityService.validateContent(content);

            assertFalse(result.isValid(), "Validation should indicate that content is not valid");
            assertEquals("Content fails XML complexity check",
                         result.getError(),
                         "Error message does not match expected value");

            verify(mockXmlComplexityServiceHelper).getXmlComplexityRules(mockXmlComplexityConfiguration);
            verify(mockXmlComplexityServiceHelper).convertTextToXml(content);
            verify(mockXmlComplexityUtil).isComplexityRuleBroken(mockDocument, maxNodesRule);
            verify(mockXmlComplexityUtil, never()).isComplexityRuleBroken(mockDocument, maxAttributesRule);
        }
    }

    @Nested
    class ComplexityRulesNotPresentTest {

        @BeforeEach
        void setUp() {
            complexityRules = new ArrayList<>();

            when(mockXmlComplexityServiceHelper.getXmlComplexityRules(mockXmlComplexityConfiguration))
                .thenReturn(complexityRules);

            xmlComplexityService = new XmlComplexityService(mockXmlComplexityConfiguration,
                                                            mockXmlComplexityServiceHelper,
                                                            mockXmlComplexityUtil);
        }

        @Test
        void testContentNoRules() {
            String content = "<root><child1/><child2/></root>";

            when(mockXmlComplexityServiceHelper.convertTextToXml(content)).thenReturn(mockDocument);

            ValidateResult result = xmlComplexityService.validateContent(content);

            assertTrue(result.isValid(), "Validation should indicate that content is valid");
            assertEquals("", result.getError(), "No error message expected");

            verify(mockXmlComplexityServiceHelper).getXmlComplexityRules(mockXmlComplexityConfiguration);
            verify(mockXmlComplexityServiceHelper).convertTextToXml(content);
            verify(mockXmlComplexityUtil, never())
                .isComplexityRuleBroken(any(Document.class), any(XmlComplexityRule.class));
        }
    }
}

package uk.gov.hmcts.darts.apim.validate.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.darts.apim.config.ThreateningContentConfiguration;
import uk.gov.hmcts.darts.apim.validate.domain.ValidateResult;
import uk.gov.hmcts.darts.apim.validate.model.ThreateningContentRule;
import uk.gov.hmcts.darts.apim.validate.util.ThreateningContentUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ThreateningContentServiceTest {

    private static final String BAD_CONTENT_RULE_MESSAGE = "Content is bad";

    @Mock
    private ThreateningContentConfiguration mockThreateningContentConfiguration;

    @Mock
    private ThreateningContentUtil mockThreateningContentUtil;

    private List<ThreateningContentRule> threateningContentRules;

    /** Class being tested. */
    private ThreateningContentService threateningContentService;

    @Nested
    class ContentRulesPresentTest {

        @BeforeEach
        void setUp() {
            threateningContentRules = new ArrayList<>();
            ThreateningContentRule badContentRule =
                new ThreateningContentRule("badContentRule", ".*bad.*", BAD_CONTENT_RULE_MESSAGE);
            threateningContentRules.add(badContentRule);
            ThreateningContentRule awfulContentRule =
                new ThreateningContentRule("awfulContentRule", ".*awful.*", "Content is awful");
            threateningContentRules.add(awfulContentRule);

            when(mockThreateningContentConfiguration.getThreateningContentRules()).thenReturn(threateningContentRules);

            threateningContentService = new ThreateningContentService(mockThreateningContentConfiguration,
                                                                      mockThreateningContentUtil);
        }

        @Test
        void testContentValid() {
            String content = "some good content";

            final ThreateningContentRule badContentRule = threateningContentRules.get(0);
            final ThreateningContentRule awfulContentRule = threateningContentRules.get(1);

            when(mockThreateningContentUtil.containsThreateningContent(content, badContentRule)).thenReturn(false);
            when(mockThreateningContentUtil.containsThreateningContent(content, awfulContentRule)).thenReturn(false);

            ValidateResult validateResult = threateningContentService.validateContent(content);

            assertTrue(validateResult.isValid(), "Validation should indicate that content is valid");
            assertEquals("", validateResult.getError(), "No error message expected");

            verify(mockThreateningContentConfiguration).getThreateningContentRules();
            verify(mockThreateningContentUtil).containsThreateningContent(content, badContentRule);
            verify(mockThreateningContentUtil).containsThreateningContent(content, awfulContentRule);
        }

        @Test
        void testContentInvalid() {
            String content = "some bad content";

            final ThreateningContentRule badContentRule = threateningContentRules.get(0);
            final ThreateningContentRule awfulContentRule = threateningContentRules.get(1);

            when(mockThreateningContentUtil.containsThreateningContent(content, badContentRule)).thenReturn(true);

            ValidateResult validateResult = threateningContentService.validateContent(content);

            assertFalse(validateResult.isValid(), "Validation should indicate that content is not valid");
            assertEquals(
                BAD_CONTENT_RULE_MESSAGE,
                validateResult.getError(),
                "Error message does not match expected value");

            verify(mockThreateningContentConfiguration).getThreateningContentRules();
            verify(mockThreateningContentUtil).containsThreateningContent(content, badContentRule);
            verify(mockThreateningContentUtil, never()).containsThreateningContent(content, awfulContentRule);
        }
    }

    @Nested
    class ContentRulesNotPresentTest {

        @BeforeEach
        void setUp() {
            threateningContentRules = new ArrayList<>();

            when(mockThreateningContentConfiguration.getThreateningContentRules()).thenReturn(threateningContentRules);

            threateningContentService = new ThreateningContentService(mockThreateningContentConfiguration,
                                                                      mockThreateningContentUtil);
        }

        @Test
        void testContentNoRules() {
            String content = "some content";

            ValidateResult validateResult = threateningContentService.validateContent(content);

            assertTrue(validateResult.isValid(), "Validation should indicate that content is valid");
            assertEquals("", validateResult.getError(), "No error message expected");

            verify(mockThreateningContentConfiguration).getThreateningContentRules();
            verify(mockThreateningContentUtil, never())
                .containsThreateningContent(anyString(), any(ThreateningContentRule.class));
        }
    }
}

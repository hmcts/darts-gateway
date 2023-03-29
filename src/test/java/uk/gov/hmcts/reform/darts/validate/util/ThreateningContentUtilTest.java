package uk.gov.hmcts.reform.darts.validate.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.darts.validate.model.ThreateningContentRule;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ThreateningContentUtilTest {

    @Mock
    ThreateningContentRule mockThreateningContentRule;

    /** Class being tested. */
    ThreateningContentUtil threateningContentUtil;

    @BeforeEach
    void setUp() {
        Pattern testPattern = Pattern.compile(".*bad.*");
        when(mockThreateningContentRule.getPattern()).thenReturn(testPattern);

        threateningContentUtil = new ThreateningContentUtil();
    }

    @Test
    void testContainsThreateningContent() {
        boolean containsThreateningContent = checkContainsThreateningContent("some bad content");
        assertTrue(containsThreateningContent, "Threatening content should be found");
        verify(mockThreateningContentRule).getPattern();
    }

    @Test
    void testDoesNotContainThreateningContent() {
        boolean containsThreateningContent = checkContainsThreateningContent("some good content");
        assertFalse(containsThreateningContent, "Threatening content should not be found");
        verify(mockThreateningContentRule).getPattern();
    }

    private boolean checkContainsThreateningContent(String text) {
        return threateningContentUtil.containsThreateningContent(text, mockThreateningContentRule);
    }
}

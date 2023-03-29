package uk.gov.hmcts.reform.darts.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.darts.Application;
import uk.gov.hmcts.reform.darts.config.ThreateningContentConfiguration;
import uk.gov.hmcts.reform.darts.validate.domain.ValidateResult;
import uk.gov.hmcts.reform.darts.validate.services.ThreateningContentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {Application.class, ThreateningContentConfiguration.class})
class ThreateningContentServiceIntTest {

    private static final String ERROR_ASCII_CONTROL_CHARACTERS = "Text contains ASCII control character";
    private static final String ERROR_CDATA = "Text contains CDATA";
    private static final String ERROR_DOUBLE_COMMA = "Text contains double comma";
    private static final String ERROR_DOCTYPE = "Text contains DOCTYPE";
    private static final String ERROR_OR_ATTACK = "Text contains OR attack";
    private static final String ERROR_PERCENTAGE_ATTACK = "Text contains percentage attack";
    private static final String ERROR_SQL_DELETE_ATTACK = "Text contains SQL delete attack";
    private static final String ERROR_SQL_DROP_TABLE_ATTACK = "Text contains SQL drop table attack";
    private static final String ERROR_SQL_INSERT_ATTACK = "Text contains SQL insert attack";
    private static final String ERROR_SQL_SERVER_SHUTDOWN_ATTACK = "Text contains SQL server shutdown attack";
    private static final String ERROR_SQL_UPDATE_ATTACK = "Text contains SQL update attack";
    private static final String ERROR_COLON_EQUALS = "Text contains colon equals";

    @Autowired
    private ThreateningContentService threateningContentService;

    private final List<ThreateningContentTestData> testData = new ArrayList<>();

    @Test
    void testValidateContent() {
        setUpTestData();

        ValidateResult result;
        for (ThreateningContentTestData test : testData) {

            result = threateningContentService.validateContent(test.getContent());

            if (test.isContentValid()) {
                assertTrue(result.isValid(), "Test [" + test.getName() + "] failed: content should be valid");
            } else {
                assertFalse(result.isValid(), "Test [" + test.getName() + "] failed: content should be invalid");
            }
            assertEquals(test.getErrorMessage(),
                         result.getError(),
                         "Test [" + test.getName() + "] failed: unexpected error message");
        }
    }

    @AllArgsConstructor
    private static class ThreateningContentTestData {

        @Getter
        private String name;

        @Getter
        private String content;

        private boolean contentValid;

        @Getter
        private String errorMessage;

        public boolean isContentValid() {
            return contentValid;
        }
    }

    private void createTestData(String name, String content, boolean contentValid, String errorMessage) {
        testData.add(new ThreateningContentTestData(name, content, contentValid, errorMessage));
    }

    private void createTestData(String name,
                                int controlCharAsHex,
                                boolean contentValid,
                                String errorMessage) {
        createTestData(name, Character.toString(controlCharAsHex), contentValid, errorMessage);
    }

    private void createTestDataCaseInsensitive(String name,
                                               String content,
                                               boolean contentValid,
                                               String errorMessage) {
        createTestData(name + "Lower", content.toLowerCase(Locale.ROOT), contentValid, errorMessage);
        createTestData(name + "Upper", content.toUpperCase(Locale.ROOT), contentValid, errorMessage);
    }

    private void setUpTestData() {
        createTestDataCaseInsensitive("contentGood","good content", true, "");
        createTestDataCaseInsensitive("cdata", "<![CDATA[", false, ERROR_CDATA);
        createTestData("doubleComma", ",,", false, ERROR_DOUBLE_COMMA);
        createTestDataCaseInsensitive("doctype", "<!DOCTYPE", false, ERROR_DOCTYPE);
        createTestDataCaseInsensitive("orAttack", "' or", false, ERROR_OR_ATTACK);
        createTestDataCaseInsensitive("sqlDeleteAttack", "'; delete", false, ERROR_SQL_DELETE_ATTACK);
        createTestDataCaseInsensitive("sqlDropTableAttack", "'; drop table", false, ERROR_SQL_DROP_TABLE_ATTACK);
        createTestDataCaseInsensitive("sqlInsertAttack", "'; insert", false, ERROR_SQL_INSERT_ATTACK);
        createTestDataCaseInsensitive("sqlServerShutdownAttack",
                                      "'; shutdown with nowait",
                                      false,
                                      ERROR_SQL_SERVER_SHUTDOWN_ATTACK);
        createTestDataCaseInsensitive("sqlUpdateAttack", "'; update", false, ERROR_SQL_UPDATE_ATTACK);
        createTestData("colonEquals", ":=", false, ERROR_COLON_EQUALS);

        setUpControlCharTestData();
        setUpPercentageAttackTestData();
    }

    private void setUpControlCharTestData() {

        /*
        Content containing any ASCII control characters in the range 0 (0x00) to 31 (0x1F) excluding
        line feed (0x0A) and carriage return (0x0D) should be flagged as invalid.  In addition, content
        containing the delete control character (x07F) should also be flagged as invalid.
        */

        final String testNamePrefix = "asciiControlChar";
        String controlCharTestName;

        for (int controlChar = 0x00; controlChar <= 0x1F; controlChar++) {
            controlCharTestName = testNamePrefix + controlChar;
            if (controlChar == 0x0A || controlChar == 0x0D) {
                createTestData(controlCharTestName, controlChar, true, "");
            } else {
                createTestData(controlCharTestName, controlChar, false, ERROR_ASCII_CONTROL_CHARACTERS);
            }
        }

        int controlCharDelete = 0x7F;
        controlCharTestName = testNamePrefix + controlCharDelete;
        createTestData(controlCharTestName, controlCharDelete, false, ERROR_ASCII_CONTROL_CHARACTERS);
    }

    private void setUpPercentageAttackTestData() {

        // Content containing % followed by any of the letters A-F or digits 0-9 should be flagged as invalid.
        final String percentage = "%";
        final String testNamePrefix = "percentageAttack%";

        for (int suffix = 0; suffix <= 9; suffix++) {
            createTestData(testNamePrefix + suffix, percentage + suffix, false, ERROR_PERCENTAGE_ATTACK);
        }

        createTestDataCaseInsensitive(testNamePrefix + "A", percentage + "A", false, ERROR_PERCENTAGE_ATTACK);
        createTestDataCaseInsensitive(testNamePrefix + "B", percentage + "B", false, ERROR_PERCENTAGE_ATTACK);
        createTestDataCaseInsensitive(testNamePrefix + "C", percentage + "C", false, ERROR_PERCENTAGE_ATTACK);
        createTestDataCaseInsensitive(testNamePrefix + "D", percentage + "D", false, ERROR_PERCENTAGE_ATTACK);
        createTestDataCaseInsensitive(testNamePrefix + "E", percentage + "E", false, ERROR_PERCENTAGE_ATTACK);
        createTestDataCaseInsensitive(testNamePrefix + "F", percentage + "F", false, ERROR_PERCENTAGE_ATTACK);

        createTestDataCaseInsensitive(testNamePrefix + "G", percentage + "G", true, "");
    }
}

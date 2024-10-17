package uk.gov.hmcts.darts.log.service.impl;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.slf4j.event.Level;
import uk.gov.hmcts.darts.log.service.DarNotificationLoggerService;

import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.event.Level.DEBUG;
import static org.slf4j.event.Level.ERROR;
import static org.slf4j.event.Level.INFO;
import static org.slf4j.event.Level.TRACE;
import static org.slf4j.event.Level.WARN;

class DarNotificationLoggerServiceImplTest {

    private static final Map<Level, Supplier<List<String>>> LOG_LEVEL_TO_CAPTURED_LOGS = new EnumMap<>(Level.class);
    private static final String SOME_TS_STRING = "2021-01-01T02:00:00Z";
    private static final OffsetDateTime SOME_TS = OffsetDateTime.parse(SOME_TS_STRING);
    private static final String SOME_URI = "some-uri";
    private static final String SOME_COURTHOUSE = "some-courthouse";
    private static final String SOME_COURTROOM = "some-courtroom";
    private static final String SOME_CASE_NUMBER = "some-case-number";

    private static LogCaptor logCaptor;

    private DarNotificationLoggerService notificationLogger;

    @BeforeAll
    public static void setupLogCaptor() {
        logCaptor = LogCaptor.forClass(DarNotificationLoggerServiceImpl.class);
        logCaptor.setLogLevelToTrace();

        LOG_LEVEL_TO_CAPTURED_LOGS.put(WARN, logCaptor::getWarnLogs);
        LOG_LEVEL_TO_CAPTURED_LOGS.put(ERROR, logCaptor::getErrorLogs);
        LOG_LEVEL_TO_CAPTURED_LOGS.put(INFO, logCaptor::getInfoLogs);
        LOG_LEVEL_TO_CAPTURED_LOGS.put(DEBUG, logCaptor::getDebugLogs);
        LOG_LEVEL_TO_CAPTURED_LOGS.put(TRACE, logCaptor::getTraceLogs);
    }

    @AfterEach
    public void clearLogs() {
        logCaptor.clearLogs();
    }

    @AfterAll
    public static void tearDown() {
        logCaptor.close();
    }

    @BeforeEach
    void setUp() {
        notificationLogger = new DarNotificationLoggerServiceImpl();
    }

    @Test
    void logsSuccess() {
        notificationLogger.notificationSucceeded(
            SOME_URI, SOME_COURTHOUSE, SOME_COURTROOM, SOME_CASE_NUMBER, SOME_TS, 0);

        String expectedLogEntry = format("DAR Notify: uri=%s, courthouse=%s, courtroom=%s, caseNumber=%s, date_time=%s, response_status=OK, responseCode=0",
                                         SOME_URI, SOME_COURTHOUSE, SOME_COURTROOM, SOME_CASE_NUMBER, SOME_TS_STRING);
        assertThat(logCaptor.getInfoLogs()).containsExactly(expectedLogEntry);
    }

    @ParameterizedTest
    @EnumSource(Level.class)
    void logsFailureWithMessage(Level logLevel) {
        notificationLogger.notificationFailed(
            SOME_URI, SOME_COURTHOUSE, SOME_COURTROOM, SOME_CASE_NUMBER, SOME_TS, "FAILED", "some-msg", logLevel);

        String expectedLogEntry = format("DAR Notify: uri=%s, courthouse=%s, courtroom=%s, caseNumber=%s, date_time=%s, response_status=FAILED, " +
                                             "message=some-msg", SOME_URI, SOME_COURTHOUSE, SOME_COURTROOM, SOME_CASE_NUMBER, SOME_TS_STRING);
        assertThat(logsFor(logLevel)).containsExactly(expectedLogEntry);
    }

    @ParameterizedTest
    @EnumSource(Level.class)
    void logsFailureWithMessageAndCode(Level logLevel) {
        notificationLogger.notificationFailedWithCode(
            SOME_URI, SOME_COURTHOUSE, SOME_COURTROOM, SOME_CASE_NUMBER, SOME_TS, "FAILED", "some-msg", 1, logLevel);

        String expectedLogEntry =
            format("DAR Notify: uri=%s, courthouse=%s, courtroom=%s, caseNumber=%s, date_time=%s, response_status=FAILED, message=some-msg, responseCode=1",
                   SOME_URI, SOME_COURTHOUSE, SOME_COURTROOM, SOME_CASE_NUMBER, SOME_TS_STRING);
        assertThat(logsFor(logLevel)).containsExactly(expectedLogEntry);
    }

    private List<String> logsFor(Level logLevel) {
        return LOG_LEVEL_TO_CAPTURED_LOGS.get(logLevel).get();
    }
}

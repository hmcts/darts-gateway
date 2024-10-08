package uk.gov.hmcts.darts.testutils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MemoryLogAppender extends AppenderBase<ILoggingEvent> {

    public static final List<ILoggingEvent> GENERAL_LOGS = new ArrayList<>();

    public static final String LOG_API_LOGGER_NAME_PACKAGE_PREFIX = "uk.gov.hmcts.darts.log";

    public void reset() {
        GENERAL_LOGS.clear();
    }

    public List<ILoggingEvent> searchLogApiLogs(String string, Level level) {
        synchronized (GENERAL_LOGS) {
            return GENERAL_LOGS.stream()
                .filter(event -> event.getLoggerName().startsWith(LOG_API_LOGGER_NAME_PACKAGE_PREFIX)
                    && level == null || level != null && level.equals(event.getLevel()))
                .collect(Collectors.toList());
        }
    }

    public List<ILoggingEvent> searchLogs(String string, String causeMessage, Level level) {
        synchronized (GENERAL_LOGS) {
            return GENERAL_LOGS.stream()
                .filter(event -> event.toString().contains(string)
                    && causeMessage == null || causeMessage != null && event.getThrowableProxy() != null
                    && event.getThrowableProxy().getMessage().contains(causeMessage)
                    && level == null || level != null && level.equals(event.getLevel()))
                .collect(Collectors.toList());
        }
    }

    public boolean hasLogApiCallTakenPlace() {
        synchronized (GENERAL_LOGS) {
            return !GENERAL_LOGS.stream()
                .filter(event -> event.getLoggerName().startsWith(LOG_API_LOGGER_NAME_PACKAGE_PREFIX)).collect(Collectors.toList()).isEmpty();
        }
    }

    @Override
    protected void append(ILoggingEvent event) {
        synchronized (GENERAL_LOGS) {
            GENERAL_LOGS.add(event);
        }
    }
}

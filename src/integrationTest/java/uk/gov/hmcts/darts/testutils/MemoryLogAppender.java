package uk.gov.hmcts.darts.testutils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MemoryLogAppender extends AppenderBase<ILoggingEvent> {

    public static final List<ILoggingEvent> GENERAL_LOGS = new ArrayList<>();

    private final Lock instanceLock = new ReentrantLock();

    public void reset() {
        GENERAL_LOGS.clear();
    }


    public List<ILoggingEvent> searchLogs(String string, String causeMessage, Level level) {
        try {
            instanceLock.lock();
            return GENERAL_LOGS.stream()
                .filter(event -> event.toString().contains(string)
                    && causeMessage == null || causeMessage != null && event.getThrowableProxy() != null
                    && event.getThrowableProxy().getMessage().contains(causeMessage)
                    && level == null || level != null && level.equals(event.getLevel()))
                .toList();
        } finally {
            instanceLock.unlock();
        }
    }


    @Override
    protected void append(ILoggingEvent event) {
        try {
            instanceLock.lock();
            GENERAL_LOGS.add(event);
        } finally {
            instanceLock.unlock();
        }
    }
}

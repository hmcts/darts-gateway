package uk.gov.hmcts.darts.cache.token.service;

import org.springframework.integration.support.locks.LockRegistry;
import uk.gov.hmcts.darts.cache.token.service.value.CacheValue;

import java.util.Optional;
import java.util.concurrent.locks.Lock;

public class CacheLockableUnitOfWork {

    private final LockRegistry lockRegistry;

    public CacheLockableUnitOfWork(LockRegistry lockRegistry) {
        this.lockRegistry = lockRegistry;
    }

    @FunctionalInterface
    interface Execute {
        void execute(Token token);
    }

    @FunctionalInterface
    interface ExecuteForToken {
        Token execute();
    }

    @FunctionalInterface
    interface ExecuteForBoolean {
        Token execute();
    }


    @FunctionalInterface
    interface ExecuteRefreshableValueReturn {
        Optional<CacheValue> execute(Token token);
    }

    public Token execute(ExecuteForBoolean runnable, String id) {
        Lock lock = lockRegistry.obtain(id);
        lock.lock();
        try {
            return runnable.execute();
        } finally {
            lock.unlock();
        }
    }
}

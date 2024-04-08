package uk.gov.hmcts.darts.cache.token.service;

import org.springframework.integration.support.locks.LockRegistry;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;
import uk.gov.hmcts.darts.cache.token.service.value.CacheValue;

import java.util.Optional;
import java.util.concurrent.locks.Lock;

public class CacheLockableUnitOfWork {

    private LockRegistry lockRegistry;

    public CacheLockableUnitOfWork(LockRegistry lockRegistry) {
        this.lockRegistry = lockRegistry;
    }

    @FunctionalInterface
    interface Execute {
        void execute(Token token) throws CacheException;
    }

    @FunctionalInterface
    interface ExecuteForToken {
        Token execute() throws CacheException;
    }

    @FunctionalInterface
    interface ExecuteForBoolean {
        Token execute() throws CacheException;
    }


    @FunctionalInterface
    interface ExecuteRefreshableValueReturn {
        Optional<CacheValue> execute(Token token) throws CacheException;
    }

    public Optional<Token> execute(ExecuteForBoolean runnable,  String id) throws CacheException {
        Lock lock = lockRegistry.obtain(id);
        lock.lock();
        try {
            return Optional.of(runnable.execute());
        } finally {
            lock.unlock();
        }
    }
}

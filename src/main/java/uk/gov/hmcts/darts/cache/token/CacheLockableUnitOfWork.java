package uk.gov.hmcts.darts.cache.token;

import org.springframework.integration.support.locks.LockRegistry;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;

import java.io.IOException;
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
    interface ExecuteValue {
        void execute(RefreshableCacheValue value) throws CacheException;
    }

    @FunctionalInterface
    interface ExecuteRefreshableValueReturn {
        Optional<RefreshableCacheValue> execute(Token token) throws CacheException;
    }

    @FunctionalInterface
    interface ExecuteTokenValueReturn {
        Optional<Token> execute(RefreshableCacheValue refresh) throws CacheException;
    }

    public void execute(Execute runnable, Token token) throws CacheException {

        Lock lock = lockRegistry.obtain(token.getId());
        lock.lock();
        try {
            runnable.execute(token);
        } finally {
            lock.unlock();
        }
    }

    public void execute(ExecuteValue runnable, RefreshableCacheValue value) throws CacheException {

        Lock lock = lockRegistry.obtain(value.getId());
        lock.lock();
        try {
            runnable.execute(value);
        } finally {
            lock.unlock();
        }
    }

    public Optional<RefreshableCacheValue> executeForRefreshValueReturn(ExecuteRefreshableValueReturn runnable, Token token) throws CacheException {
        Lock lock = lockRegistry.obtain(token.getId());
        lock.lock();
        try {
            return runnable.execute(token);
        } finally {
            lock.unlock();
        }
    }

    public Optional<Token> executeForTokenReturn(ExecuteTokenValueReturn runnable, RefreshableCacheValue token) throws CacheException {
        Lock lock = lockRegistry.obtain(token.getId());
        lock.lock();
        try {
            return runnable.execute(token);
        } finally {
            lock.unlock();
        }
    }
}

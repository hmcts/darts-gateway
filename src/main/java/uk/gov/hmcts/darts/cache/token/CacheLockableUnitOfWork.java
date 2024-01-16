package uk.gov.hmcts.darts.cache.token;

import org.springframework.integration.support.locks.LockRegistry;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;

import java.util.Optional;
import java.util.concurrent.locks.Lock;

public class CacheLockableUnitOfWork {

    private LockRegistry lockRegistry;

    public CacheLockableUnitOfWork(LockRegistry lockRegistry) {
        this.lockRegistry = lockRegistry;
    }

    protected static final String DISTRIBUTED_LOCK = "LOCK";

    @FunctionalInterface
    interface Execute {
        void execute(Token token) throws CacheException;
    }

    @FunctionalInterface
    interface ExecuteForToken {
        Optional<Token> execute() throws CacheException;
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

    public Optional<Token> execute(ExecuteForToken runnable, boolean doesLock) throws CacheException {

        if (doesLock) {
            Lock lock = lockRegistry.obtain(DISTRIBUTED_LOCK);
            lock.lock();
            try {
                return runnable.execute();
            } finally {
                lock.unlock();
            }
        } else {
            return runnable.execute();
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

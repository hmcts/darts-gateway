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
        Optional<Token> execute() throws CacheException;
    }

    @FunctionalInterface
    interface ExecuteValue {
        void execute(CacheValue value) throws CacheException;
    }

    @FunctionalInterface
    interface ExecuteRefreshableValueReturn {
        Optional<CacheValue> execute(Token token) throws CacheException;
    }

    @FunctionalInterface
    interface ExecuteTokenValueReturn {
        Optional<Token> execute(CacheValue refresh) throws CacheException;
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

    public Optional<Token> execute(ExecuteForToken runnable, boolean doesLock, String id) throws CacheException {

        if (doesLock) {
            Lock lock = lockRegistry.obtain(id);
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

    public Optional<CacheValue> executeForRefreshValueReturn(ExecuteRefreshableValueReturn runnable, Token token) throws CacheException {
        Lock lock = lockRegistry.obtain(token.getId());
        lock.lock();
        try {
            return runnable.execute(token);
        } finally {
            lock.unlock();
        }
    }

    public Optional<Token> executeForTokenReturn(ExecuteTokenValueReturn runnable, CacheValue token) throws CacheException {
        Lock lock = lockRegistry.obtain(token.getId());
        lock.lock();
        try {
            return runnable.execute(token);
        } finally {
            lock.unlock();
        }
    }
}

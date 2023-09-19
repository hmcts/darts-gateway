package uk.gov.hmcts.darts.config;

import org.springframework.beans.factory.ObjectProvider;

import java.util.function.Consumer;

class EmptyObjectProvider<T> implements ObjectProvider<T> {

    @Override
    public T getObject(Object... args)  {
        return null;
    }

    @Override
    public T getObject() {
        return null;
    }

    @Override
    public void forEach(Consumer action) {
        // do nothing
    }

    @Override
    public T getIfAvailable() {
        return null;
    }

    @Override
    public T getIfUnique() {
        return null;
    }
}

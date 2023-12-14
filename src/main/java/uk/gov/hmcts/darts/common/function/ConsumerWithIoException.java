package uk.gov.hmcts.darts.common.function;

import java.io.IOException;

@FunctionalInterface
public interface ConsumerWithIoException<T> {
    void accept(T value) throws IOException;
}

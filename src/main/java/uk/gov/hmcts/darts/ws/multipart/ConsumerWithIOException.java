package uk.gov.hmcts.darts.ws.multipart;

import java.io.IOException;
import java.util.function.Consumer;

@FunctionalInterface
public interface ConsumerWithIOException <T> {

    void accept(T value) throws IOException;
}
